package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService{

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketMapper ticketMapper){
        this.ticketRepository= ticketRepository;
        this.ticketMapper= ticketMapper;
    }

    @Override
    public List<TicketResponseDto> findAll() {
        return ticketMapper.toResponseDtoList(ticketRepository.findAll());
    }

    @Override
    public TicketResponseDto findById(Long id) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(()->new TicketNotFoundException(id));
        return ticketMapper.toResponseDto(ticket);
    }

    @Override
    public TicketResponseDto save(TicketRequestDto ticketRequestDto) {
        TicketEntity ticketEntity= ticketMapper.toEntity(ticketRequestDto);
        return ticketMapper.toResponseDto(ticketRepository.save(ticketEntity));
    }

    @Override
    public TicketResponseDto update(Long id, TicketRequestDto updatedTicketRequestDto) {
        TicketEntity ticket = ticketRepository.findById(id)
                .orElseThrow(()->new TicketNotFoundException(id));

        /*
        ticket.setStatus(updatedTicketRequestDto.status());
        ticket.setTitle(updatedTicketRequestDto.title());
        ticket.setDescription(updatedTicketRequestDto.description());
        ticket.setRequesterName(updatedTicketRequestDto.requesterName());
        ticket.setRequesterEmail(updatedTicketRequestDto.requesterEmail());
        ticket.setPriority(updatedTicketRequestDto.priority());*/

        ticketMapper.updateEntityFromDto(updatedTicketRequestDto, ticket);

        TicketEntity ticketEntity= ticketRepository.save(ticket);
        return ticketMapper.toResponseDto(ticketEntity);
    }
}
