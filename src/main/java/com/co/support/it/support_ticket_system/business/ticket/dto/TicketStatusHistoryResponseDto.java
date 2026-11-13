package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;

import java.time.LocalDateTime;

public record TicketStatusHistoryResponseDto(
        Long id,
        Long ticketId,
        TicketStatus previousStatus,
        TicketStatus newStatus,
        LocalDateTime changedAt
) {
}