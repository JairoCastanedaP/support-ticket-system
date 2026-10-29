package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.dto.TicketStatusHistoryResponseDto;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<TicketResponseDto> findAll();
    TicketResponseDto findById(Long id);
    TicketResponseDto save(TicketRequestDto ticketRequestDto);
    TicketResponseDto update(Long id, TicketRequestDto ticketRequestDto);
    List<TicketStatusHistoryResponseDto> findStatusHistory(Long ticketId);
}
