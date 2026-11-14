package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketStatusHistoryEntity;
import com.co.support.it.support_ticket_system.business.ticket.exception.InvalidStatusTransitionException;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketStatusHistoryMapper;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketStatusHistoryRepository;
import com.co.support.it.support_ticket_system.business.ticket.service.status.TicketStatusChangeStrategyResolver;
import com.co.support.it.support_ticket_system.business.ticket.service.validation.TicketValidationChain;
import com.co.support.it.support_ticket_system.business.ticket.service.validation.TicketValidationContext;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository.AuditOutboxRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private static final String SOURCE_SYSTEM = "support-ticket-system";

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private final TicketMapper ticketMapper;
    private final TicketStatusHistoryMapper ticketStatusHistoryMapper;
    private final TicketValidationChain validationChain;
    private final TicketStatusChangeStrategyResolver statusStrategyResolver;
    private final AuditOutboxRepository auditOutboxRepository;
    private final JsonMapper jsonMapper;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketStatusHistoryRepository ticketStatusHistoryRepository,
            TicketMapper ticketMapper,
            TicketStatusHistoryMapper ticketStatusHistoryMapper,
            TicketValidationChain validationChain,
            TicketStatusChangeStrategyResolver statusStrategyResolver,
            AuditOutboxRepository auditOutboxRepository,
            JsonMapper jsonMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.ticketStatusHistoryRepository = ticketStatusHistoryRepository;
        this.ticketMapper = ticketMapper;
        this.ticketStatusHistoryMapper = ticketStatusHistoryMapper;
        this.validationChain = validationChain;
        this.statusStrategyResolver = statusStrategyResolver;
        this.auditOutboxRepository = auditOutboxRepository;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Page<TicketResponseDto> findAll(
            TicketStatus status,
            TicketPriority priority,
            Long categoryId,
            Long supportAgentId,
            Pageable pageable
    ) {
        Specification<TicketEntity> spec =
                (root, query, cb) -> cb.and(
                        cb.isTrue(root.get("active")),
                        cb.isNull(root.get("deletedAt"))
                );

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (priority != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priority"), priority));
        }

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }

        if (supportAgentId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assignedAgent").get("id"), supportAgentId));
        }

        return ticketRepository.findAll(spec, pageable)
                .map(ticketMapper::toResponseDto);
    }

    @Override
    public TicketResponseDto findById(Long id) {
        return ticketMapper.toResponseDto(findActiveTicket(id));
    }

    /**
     * Crea el ticket siempre en OPEN, después de pasar la cadena de validación.
     * El número legible se asigna cuando ya existe el id.
     */
    @Override
    @Transactional
    public TicketResponseDto save(TicketRequestDto request) {
        TicketValidationContext context = validationChain.validate(request);

        TicketEntity ticket = ticketMapper.toEntity(request);
        ticket.setStatus(TicketStatus.OPEN);
        applyRelations(ticket, context);

        TicketEntity savedTicket = ticketRepository.save(ticket);
        savedTicket.assignTicketNumber();
        savedTicket = ticketRepository.saveAndFlush(savedTicket);

        registerEvent("TICKET_CREATED", savedTicket, new TicketCreatedPayload(
                savedTicket.getId(),
                savedTicket.getTicketNumber(),
                savedTicket.getTitle(),
                savedTicket.getPriority(),
                savedTicket.getStatus(),
                savedTicket.getRequester().getId(),
                savedTicket.getCategory().getId(),
                savedTicket.getAssignedAgent() == null ? null : savedTicket.getAssignedAgent().getId()
        ));

        return ticketMapper.toResponseDto(savedTicket);
    }

    /**
     * Actualiza los datos del ticket. El estado no cambia aquí y un ticket cerrado no se puede editar.
     */
    @Override
    @Transactional
    public TicketResponseDto update(Long id, TicketRequestDto request) {
        TicketEntity ticket = findActiveTicket(id);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessRuleException("El ticket " + ticket.getTicketNumber() + " está cerrado y no se puede editar");
        }

        TicketValidationContext context = validationChain.validate(request);

        ticketMapper.updateEntityFromDto(request, ticket);
        applyRelations(ticket, context);

        return ticketMapper.toResponseDto(ticketRepository.saveAndFlush(ticket));
    }

    /**
     * Cambia el estado: valida la transición, aplica la estrategia del estado destino,
     * registra el historial y deja el evento en el outbox de auditoría.
     */
    @Override
    @Transactional
    public TicketResponseDto changeStatus(Long id, TicketStatusChangeRequestDto request) {
        TicketEntity ticket = findActiveTicket(id);
        TicketStatus previousStatus = ticket.getStatus();
        TicketStatus newStatus = request.status();

        if (!previousStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(previousStatus, newStatus);
        }

        statusStrategyResolver.resolve(newStatus).apply(ticket, request);
        ticket.setStatus(newStatus);

        String reason = request.reason() == null || request.reason().isBlank() ? null : request.reason().trim();

        TicketStatusHistoryEntity history = new TicketStatusHistoryEntity();
        history.setTicket(ticket);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        history.setChangedAt(LocalDateTime.now());
        ticketStatusHistoryRepository.save(history);

        TicketEntity updatedTicket = ticketRepository.saveAndFlush(ticket);

        registerEvent("TICKET_STATUS_CHANGED", updatedTicket, new TicketStatusChangedPayload(
                updatedTicket.getId(),
                updatedTicket.getTicketNumber(),
                previousStatus,
                newStatus,
                reason,
                updatedTicket.getUpdatedBy()
        ));

        return ticketMapper.toResponseDto(updatedTicket);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TicketEntity ticket = findActiveTicket(id);

        ticket.setActive(false);
        ticket.setDeletedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketStatusHistoryResponseDto> findStatusHistory(Long ticketId) {
        findActiveTicket(ticketId);

        return ticketStatusHistoryMapper.toResponseDtoList(
                ticketStatusHistoryRepository.findByTicket_IdOrderByChangedAtAsc(ticketId)
        );
    }

    private TicketEntity findActiveTicket(Long id) {
        return ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    private void applyRelations(TicketEntity ticket, TicketValidationContext context) {
        ticket.setRequester(context.getRequester());
        ticket.setCategory(context.getCategory());
        ticket.setAssignedAgent(context.getSupportAgent());
    }

    private void registerEvent(String eventType, TicketEntity ticket, Object payload) {
        AuditOutboxEntity event = new AuditOutboxEntity();
        event.setSourceSystem(SOURCE_SYSTEM);
        event.setEventType(eventType);
        event.setEntityType("TICKET");
        event.setEntityId(ticket.getId().toString());
        event.setOccurredAtUtc(Instant.now());
        event.setPayload(jsonMapper.writeValueAsString(payload));

        auditOutboxRepository.save(event);
    }

    private record TicketCreatedPayload(
            Long ticketId,
            String ticketNumber,
            String title,
            TicketPriority priority,
            TicketStatus status,
            Long requesterId,
            Long categoryId,
            Long supportAgentId
    ) {
    }

    private record TicketStatusChangedPayload(
            Long ticketId,
            String ticketNumber,
            TicketStatus previousStatus,
            TicketStatus newStatus,
            String reason,
            String changedBy
    ) {
    }
}
