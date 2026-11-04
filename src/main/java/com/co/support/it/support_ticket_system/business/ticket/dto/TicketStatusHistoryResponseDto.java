package com.co.support.it.support_ticket_system.business.ticket.dto;

import java.time.LocalDateTime;

public record TicketStatusHistoryResponseDto(
        Long id,
        Long ticketId,
        String previousStatus,
        String newStatus,
        LocalDateTime changedAt
) {
}