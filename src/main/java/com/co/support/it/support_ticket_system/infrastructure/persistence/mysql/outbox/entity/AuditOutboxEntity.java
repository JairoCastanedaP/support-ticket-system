package com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_outbox")
@Getter
@Setter
@NoArgsConstructor
public class AuditOutboxEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "source_system", nullable = false, length = 80)
    private String sourceSystem;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "occurred_at_utc", nullable = false)
    private Instant occurredAtUtc;

    @Column(name = "actor_type", length = 30)
    private String actorType;

    @Column(name = "actor_id", length = 100)
    private String actorId;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    @Column(name = "processed_at_utc")
    private Instant processedAtUtc;

    @PrePersist
    private void beforeInsert() {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAtUtc == null) {
            occurredAtUtc = Instant.now();
        }
    }

    public enum DeliveryStatus {
        PENDING,
        PROCESSED,
        FAILED
    }
}