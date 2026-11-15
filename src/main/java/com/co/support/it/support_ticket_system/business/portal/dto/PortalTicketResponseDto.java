package com.co.support.it.support_ticket_system.business.portal.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;

import java.time.LocalDateTime;

/**
 * Vista del ticket para clientes externos: sin datos personales, agente ni auditoría interna.
 */
public record PortalTicketResponseDto(
        String ticketNumber,
        String title,
        TicketStatus status,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        String resolutionNote
) {
}
