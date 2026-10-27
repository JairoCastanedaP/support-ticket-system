package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketEntity;
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
    public Optional<TicketResponseDto> findById(Long id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toResponseDto);
    }

    @Override
    public TicketResponseDto save(TicketRequestDto ticketRequestDto) {
        TicketEntity ticketEntity= ticketMapper.toEntity(ticketRequestDto);
        return ticketMapper.toResponseDto(ticketRepository.save(ticketEntity));
    }

    @Override
    public Optional<TicketResponseDto> update(Long id, TicketRequestDto updatedTicketRequestDto) {

        return ticketRepository.findById(id)
                .map(existingTicket ->{
                    ticketMapper.updateEntityFromDto(
                            updatedTicketRequestDto,
                            existingTicket
                    );
                    TicketEntity updatedTicket = ticketRepository.save(existingTicket);
                    return ticketMapper.toResponseDto(updatedTicket);
                });
    }
}
