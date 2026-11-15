package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;

public record TicketSearchResultDto(
        Long id,
        String ticketNumber,
        String title,
        TicketStatus status,
        TicketPriority priority
) {
}
