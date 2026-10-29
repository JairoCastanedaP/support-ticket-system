package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.exception.CategoryNotFoundException;
import com.co.support.it.support_ticket_system.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RequesterRepository requesterRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            RequesterRepository requesterRepository,
            CategoryRepository categoryRepository,
            TicketMapper ticketMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.requesterRepository = requesterRepository;
        this.categoryRepository = categoryRepository;
        this.ticketMapper = ticketMapper;
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

        TicketEntity savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponseDto(savedTicket);
    }

    @Override
    @Transactional
    public TicketResponseDto update(Long id, TicketRequestDto updatedTicketRequestDto) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        ticketMapper.updateEntityFromDto(updatedTicketRequestDto, ticket);

        ticket.setRequester(requesterRepository.findById(updatedTicketRequestDto.requesterId())
                .orElseThrow(() -> new RequesterNotFoundException(updatedTicketRequestDto.requesterId())));

        ticket.setCategory(categoryRepository.findById(updatedTicketRequestDto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(updatedTicketRequestDto.categoryId())));

        TicketEntity updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toResponseDto(updatedTicket);
    }
}