package com.co.support.it.support_ticket_system.business.batch.job;

import java.time.Instant;

/**
 * Evento de auditoría tal como está en Oracle (tabla audit_event).
 */
public record AuditEventRecord(
        String eventId,
        String sourceSystem,
        String eventType,
        String entityType,
        String entityId,
        Instant occurredAtUtc,
        String actorId,
        String clientIp,
        String correlationId,
        String payload
) {
}
