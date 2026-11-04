package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.dto.TicketStatusHistoryResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    Page<TicketResponseDto> findAll(
            String status,
            String priority,
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
