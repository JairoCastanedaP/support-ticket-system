package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.category.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketStatusHistoryMapper;
import com.co.support.it.support_ticket_system.business.category.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;

import com.co.support.it.support_ticket_system.business.ticket.entity.TicketStatusHistoryEntity;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketStatusHistoryRepository;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository.AuditOutboxRepository;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RequesterRepository requesterRepository;
    private final CategoryRepository categoryRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private final TicketStatusHistoryMapper ticketStatusHistoryMapper;
    private final TicketMapper ticketMapper;

    private final AuditOutboxRepository auditOutboxRepository;
    private final JsonMapper jsonMapper;


    public TicketServiceImpl(
            TicketRepository ticketRepository,
            RequesterRepository requesterRepository,
            CategoryRepository categoryRepository,
            SupportAgentRepository supportAgentRepository,
            TicketStatusHistoryRepository ticketStatusHistoryRepository,
            TicketMapper ticketMapper,
            TicketStatusHistoryMapper ticketStatusHistoryMapper,
            AuditOutboxRepository auditOutboxRepository,
            JsonMapper jsonMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.requesterRepository = requesterRepository;
        this.categoryRepository = categoryRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.ticketStatusHistoryRepository = ticketStatusHistoryRepository;
        this.ticketMapper = ticketMapper;
        this.ticketStatusHistoryMapper = ticketStatusHistoryMapper;
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
        TicketEntity ticket = ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return ticketMapper.toResponseDto(ticket);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TicketEntity ticket =
                ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new TicketNotFoundException(id));

        ticket.setActive(false);
        ticket.setDeletedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketResponseDto save(TicketRequestDto ticketRequestDto) {
        TicketEntity ticket = ticketMapper.toEntity(ticketRequestDto);

        ticket.setRequester(requesterRepository.findById(ticketRequestDto.requesterId())
                .orElseThrow(() -> new RequesterNotFoundException(ticketRequestDto.requesterId())));

        ticket.setCategory(categoryRepository.findById(ticketRequestDto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(ticketRequestDto.categoryId())));

        if (ticketRequestDto.supportAgentId() != null) {
            ticket.setAssignedAgent(findSupportAgent(ticketRequestDto.supportAgentId()));
        }

        TicketEntity savedTicket = ticketRepository.save(ticket);

        registerTicketCreatedEvent(savedTicket);

        return ticketMapper.toResponseDto(savedTicket);
    }

    @Override
    @Transactional
    public TicketResponseDto update(Long id, TicketRequestDto updatedTicketRequestDto) {
        TicketEntity ticket = ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        TicketStatus previousStatus = ticket.getStatus();

        ticketMapper.updateEntityFromDto(updatedTicketRequestDto, ticket);

        ticket.setRequester(requesterRepository.findById(updatedTicketRequestDto.requesterId())
                .orElseThrow(() -> new RequesterNotFoundException(updatedTicketRequestDto.requesterId())));

        ticket.setCategory(categoryRepository.findById(updatedTicketRequestDto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(updatedTicketRequestDto.categoryId())));

        ticket.setAssignedAgent(
                updatedTicketRequestDto.supportAgentId() == null
                        ? null
                        : findSupportAgent(updatedTicketRequestDto.supportAgentId())
        );

        if (previousStatus != ticket.getStatus()) {
            TicketStatusHistoryEntity history = new TicketStatusHistoryEntity();
            history.setTicket(ticket);
            history.setPreviousStatus(previousStatus);
            history.setNewStatus(ticket.getStatus());
            history.setChangedAt(LocalDateTime.now());

            ticketStatusHistoryRepository.save(history);
        }

        TicketEntity updatedTicket = ticketRepository.saveAndFlush(ticket);
        return ticketMapper.toResponseDto(updatedTicket);
    }
    @Override
    public List<TicketStatusHistoryResponseDto> findStatusHistory(Long ticketId) {
        ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        return ticketStatusHistoryMapper.toResponseDtoList(
                ticketStatusHistoryRepository
                        .findByTicket_IdOrderByChangedAtAsc(ticketId)
        );
    }

    private SupportAgentEntity findSupportAgent(Long id) {
        return supportAgentRepository.findById(id)
                .orElseThrow(() -> new SupportAgentNotFoundException(id));
    }
    private void registerTicketCreatedEvent(TicketEntity ticket) {
        TicketCreatedPayload payload = new TicketCreatedPayload(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getRequester().getId(),
                ticket.getCategory().getId(),
                ticket.getAssignedAgent() == null
                        ? null
                        : ticket.getAssignedAgent().getId()
        );

        AuditOutboxEntity event = new AuditOutboxEntity();
        event.setSourceSystem("support-ticket-system");
        event.setEventType("TICKET_CREATED");
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
}