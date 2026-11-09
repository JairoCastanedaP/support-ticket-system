package com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "audit_event")
@Getter
@Setter
@NoArgsConstructor
public class AuditEventEntity {

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

    @Lob
    @Column(name = "payload")
    private String payload;
}