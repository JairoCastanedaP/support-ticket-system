package com.co.support.it.support_ticket_system.business.ticket.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Indicadores de tickets activos. {@code averageResolutionHours} es null si aún no hay tickets resueltos.
 */
public record TicketIndicatorsResponseDto(
        long totalTickets,
        Map<String, Long> byStatus,
        Map<String, Long> byCategory,
        BigDecimal averageResolutionHours
) {
}
