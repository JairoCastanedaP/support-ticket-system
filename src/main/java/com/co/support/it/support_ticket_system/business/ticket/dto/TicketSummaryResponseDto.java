package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;

import java.time.LocalDateTime;

public record TicketSummaryResponseDto(
        Long ticketId,
        String ticketNumber,
        String title,
        TicketStatus status,
        TicketPriority priority,
        String categoryName,
        String requesterFullName,
        String requesterEmail,
        String agentFullName,
        Long commentCount,
        Long ageHours,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
}
