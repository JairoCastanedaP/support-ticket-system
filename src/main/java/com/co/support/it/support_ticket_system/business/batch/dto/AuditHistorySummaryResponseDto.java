package com.co.support.it.support_ticket_system.business.batch.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Lo que hay en el histórico: totales por categoría y tipo de evento, y la fecha del último evento copiado.
 */
public record AuditHistorySummaryResponseDto(
        long totalEvents,
        Instant lastOccurredAt,
        Map<String, Long> byCategory,
        Map<String, Long> byEventType
) {
}
