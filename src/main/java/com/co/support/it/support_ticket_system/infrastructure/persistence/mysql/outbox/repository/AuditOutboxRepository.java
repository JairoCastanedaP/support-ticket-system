package com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuditOutboxRepository
        extends JpaRepository<AuditOutboxEntity, String> {

    List<AuditOutboxEntity> findByDeliveryStatusOrderByOccurredAtUtcAsc(
            AuditOutboxEntity.DeliveryStatus deliveryStatus
    );

    @Transactional(readOnly = true)
    List<AuditOutboxEntity> findTop50ByDeliveryStatusOrderByOccurredAtUtcAsc(
            AuditOutboxEntity.DeliveryStatus deliveryStatus
    );
}