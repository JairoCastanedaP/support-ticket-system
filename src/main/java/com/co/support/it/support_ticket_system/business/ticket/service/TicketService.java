package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {
    Page<TicketResponseDto> findAll(
            TicketStatus status,
            TicketPriority priority,
            Long categoryId,
            Long supportAgentId,
            Pageable pageable
    );
    TicketResponseDto findById(Long id);
    TicketResponseDto save(TicketRequestDto ticketRequestDto);
    TicketResponseDto update(Long id, TicketRequestDto ticketRequestDto);
    void delete(Long id);
    List<TicketStatusHistoryResponseDto> findStatusHistory(Long ticketId);
}
