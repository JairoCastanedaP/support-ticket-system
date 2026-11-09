package com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.repository;

import com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {
}