package com.co.support.it.support_ticket_system.business.batch.dto;

import java.time.OffsetDateTime;

/**
 * Resultado de una ejecución del volcado. alreadyCopiedCount son los eventos leídos que ya estaban en el histórico.
 */
public record AuditHistoryRunResponseDto(
        Long executionId,
        String status,
        String requestedBy,
        OffsetDateTime fromOccurredAt,
        int chunkSize,
        long readCount,
        long alreadyCopiedCount,
        long writeCount,
        long chunkCount,
        long durationMillis
) {
}
