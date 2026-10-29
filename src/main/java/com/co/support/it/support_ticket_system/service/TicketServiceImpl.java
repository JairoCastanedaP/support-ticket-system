package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.mapper.TicketStatusHistoryMapper;
import com.co.support.it.support_ticket_system.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.co.support.it.support_ticket_system.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.repository.SupportAgentRepository;

import com.co.support.it.support_ticket_system.entity.TicketStatusHistoryEntity;
import com.co.support.it.support_ticket_system.repository.TicketStatusHistoryRepository;

import com.co.support.it.support_ticket_system.dto.TicketStatusHistoryResponseDto;

import java.time.LocalDateTime;
import java.util.List;

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


    public TicketServiceImpl(
            TicketRepository ticketRepository,
            RequesterRepository requesterRepository,
            CategoryRepository categoryRepository,
            SupportAgentRepository supportAgentRepository,
            TicketStatusHistoryRepository ticketStatusHistoryRepository,
            TicketMapper ticketMapper,
            TicketStatusHistoryMapper ticketStatusHistoryMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.requesterRepository = requesterRepository;
        this.categoryRepository = categoryRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.ticketStatusHistoryRepository = ticketStatusHistoryRepository;
        this.ticketMapper = ticketMapper;
        this.ticketStatusHistoryMapper = ticketStatusHistoryMapper;
    }

    @Override
    public List<TicketResponseDto> findAll() {
        return ticketMapper.toResponseDtoList(ticketRepository.findAll());
    }

    @Override
    public TicketResponseDto findById(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        return ticketMapper.toResponseDto(ticket);
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
        return ticketMapper.toResponseDto(savedTicket);
    }

    @Override
    @Transactional
    public TicketResponseDto update(Long id, TicketRequestDto updatedTicketRequestDto) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        String previousStatus = ticket.getStatus();

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

        if (!previousStatus.equals(ticket.getStatus())) {
            TicketStatusHistoryEntity history = new TicketStatusHistoryEntity();
            history.setTicket(ticket);
            history.setPreviousStatus(previousStatus);
            history.setNewStatus(ticket.getStatus());
            history.setChangedAt(LocalDateTime.now());

            ticketStatusHistoryRepository.save(history);
        }

        TicketEntity updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponseDto(updatedTicket);
    }
    @Override
    public List<TicketStatusHistoryResponseDto> findStatusHistory(Long ticketId) {
        ticketRepository.findById(ticketId)
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
}