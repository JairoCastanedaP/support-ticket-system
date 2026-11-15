package com.co.support.it.support_ticket_system.business.batch.job;

import java.time.OffsetDateTime;

/**
 * Registro del histórico (tabla audit_event_history): el evento clasificado y con sus datos principales extraídos.
 */
public record AuditHistoryRecord(
        String eventId,
        String sourceSystem,
        String eventType,
        String eventCategory,
        String entityType,
        String entityId,
        OffsetDateTime occurredAtUtc,
        String actorId,
        String clientIp,
        String correlationId,
        String httpMethod,
        String httpPath,
        Integer httpStatus,
        Long durationMillis,
        String ticketNumber,
        String previousStatus,
        String newStatus,
        OffsetDateTime exportedAtUtc,
        Long jobExecutionId
) {
}
