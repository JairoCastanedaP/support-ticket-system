package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<TicketResponseDto> findAll();
    Optional<TicketResponseDto> findById(Long id);
    TicketResponseDto save(TicketRequestDto ticketRequestDto);
    Optional<TicketResponseDto> update(Long id, TicketRequestDto ticketRequestDto);
}
