package com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditOutboxRepository
        extends JpaRepository<AuditOutboxEntity, String> {

    List<AuditOutboxEntity> findByDeliveryStatusOrderByOccurredAtUtcAsc(
            AuditOutboxEntity.DeliveryStatus deliveryStatus
    );
}