package com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.service;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.entity.AuditEventEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OracleAuditEventWriter {

    private final AuditEventRepository auditEventRepository;

    public OracleAuditEventWriter(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional(transactionManager = "oracleAuditTransactionManager")
    public void storeIfAbsent(AuditOutboxEntity outboxEvent) {
        if (auditEventRepository.existsById(outboxEvent.getEventId())) {
            return;
        }

        AuditEventEntity auditEvent = new AuditEventEntity();
        auditEvent.setEventId(outboxEvent.getEventId());
        auditEvent.setSourceSystem(outboxEvent.getSourceSystem());
        auditEvent.setEventType(outboxEvent.getEventType());
        auditEvent.setEntityType(outboxEvent.getEntityType());
        auditEvent.setEntityId(outboxEvent.getEntityId());
        auditEvent.setOccurredAtUtc(outboxEvent.getOccurredAtUtc());
        auditEvent.setActorType(outboxEvent.getActorType());
        auditEvent.setActorId(outboxEvent.getActorId());
        auditEvent.setClientIp(outboxEvent.getClientIp());
        auditEvent.setCorrelationId(outboxEvent.getCorrelationId());
        auditEvent.setPayload(outboxEvent.getPayload());

        auditEventRepository.saveAndFlush(auditEvent);
    }
}