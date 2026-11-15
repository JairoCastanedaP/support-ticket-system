package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketSummaryView;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketQueryMapper;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.IndicatorRow;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.TicketIndicatorsResult;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketSummaryViewRepository;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import com.co.support.it.support_ticket_system.generic.exception.FeatureNotAvailableException;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.integration.asset.AssetCatalogClient;
import com.co.support.it.support_ticket_system.integration.asset.AssetNotFoundException;
import com.co.support.it.support_ticket_system.integration.asset.AssetResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TicketQueryServiceImpl implements TicketQueryService {

    private static final int MIN_SEARCH_LENGTH = 3;
    private static final int MAX_SEARCH_LENGTH = 100;

    private final TicketRepository ticketRepository;
    private final TicketSummaryViewRepository summaryViewRepository;
    private final TicketIndicatorsRepository indicatorsRepository;
    private final TicketMapper ticketMapper;
    private final TicketQueryMapper queryMapper;
    private final AssetCatalogClient assetCatalogClient;
    private final boolean storedProceduresEnabled;

    public TicketQueryServiceImpl(
            TicketRepository ticketRepository,
            TicketSummaryViewRepository summaryViewRepository,
            TicketIndicatorsRepository indicatorsRepository,
            TicketMapper ticketMapper,
            TicketQueryMapper queryMapper,
            AssetCatalogClient assetCatalogClient,
            @Value("${app.persistence.stored-procedures-enabled:false}") boolean storedProceduresEnabled
    ) {
        this.ticketRepository = ticketRepository;
        this.summaryViewRepository = summaryViewRepository;
        this.indicatorsRepository = indicatorsRepository;
        this.ticketMapper = ticketMapper;
        this.queryMapper = queryMapper;
        this.assetCatalogClient = assetCatalogClient;
        this.storedProceduresEnabled = storedProceduresEnabled;
    }

    @Override
    public TicketResponseDto findByTicketNumber(String ticketNumber) {
        return ticketRepository.findActiveByTicketNumber(ticketNumber.trim())
                .map(ticketMapper::toResponseDto)
                .orElseThrow(() -> new TicketNotFoundException(ticketNumber));
    }

    @Override
    public List<TicketSearchResultDto> search(String text) {
        String value = text == null ? "" : text.trim();
        if (value.length() < MIN_SEARCH_LENGTH || value.length() > MAX_SEARCH_LENGTH) {
            throw new BadRequestException("El texto de búsqueda debe tener entre "
                    + MIN_SEARCH_LENGTH + " y " + MAX_SEARCH_LENGTH + " caracteres");
        }

        return ticketRepository.searchByText(value).stream()
                .map(queryMapper::toSearchResultDto)
                .toList();
    }

    @Override
    public Page<TicketSummaryResponseDto> findSummary(TicketStatus status, TicketPriority priority, Pageable pageable) {
        Specification<TicketSummaryView> spec = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }

        return summaryViewRepository.findAll(spec, pageable).map(queryMapper::toSummaryDto);
    }

    /**
     * Ejecuta el procedimiento y organiza sus filas: todos los estados aparecen (en 0 si no hay tickets)
     * y las categorías conservan el orden alfabético del procedimiento.
     */
    @Override
    public TicketIndicatorsResponseDto getIndicators() {
        if (!storedProceduresEnabled) {
            throw new FeatureNotAvailableException(
                    "Los indicadores usan un procedimiento almacenado de MySQL y no están disponibles en el perfil local");
        }

        TicketIndicatorsResult result = indicatorsRepository.callIndicatorsProcedure();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            byStatus.put(status.name(), 0L);
        }
        Map<String, Long> byCategory = new LinkedHashMap<>();

        for (IndicatorRow row : result.rows()) {
            switch (row.dimension()) {
                case "STATUS" -> byStatus.put(row.item(), row.total());
                case "CATEGORY" -> byCategory.put(row.item(), row.total());
                default -> throw new IllegalStateException("Dimensión desconocida: " + row.dimension());
            }
        }

        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();

        return new TicketIndicatorsResponseDto(total, byStatus, byCategory, result.averageResolutionHours());
    }

    @Override
    public AssetResponseDto findAsset(Long ticketId) {
        TicketEntity ticket = ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getAssetId() == null) {
            throw new NotFoundException("El ticket " + ticket.getTicketNumber() + " no tiene un activo de TI asociado");
        }

        return assetCatalogClient.findById(ticket.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException(ticket.getAssetId()));
    }
}
