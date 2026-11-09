package com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.service;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository.AuditOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditOutboxStatusService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_ERROR_LENGTH = 2000;

    private final AuditOutboxRepository auditOutboxRepository;

    public AuditOutboxStatusService(
            AuditOutboxRepository auditOutboxRepository
    ) {
        this.auditOutboxRepository = auditOutboxRepository;
    }

    @Transactional
    public void markProcessed(String eventId) {
        AuditOutboxEntity event = auditOutboxRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el evento outbox: " + eventId
                        )
                );

        if (event.getDeliveryStatus()
                != AuditOutboxEntity.DeliveryStatus.PENDING) {
            return;
        }

        event.setDeliveryStatus(AuditOutboxEntity.DeliveryStatus.PROCESSED);
        event.setProcessedAtUtc(Instant.now());
        event.setLastError(null);

        auditOutboxRepository.save(event);
    }

    @Transactional
    public void registerDeliveryFailure(String eventId, RuntimeException error) {
        AuditOutboxEntity event = auditOutboxRepository.findById(eventId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el evento outbox: " + eventId
                        )
                );

        if (event.getDeliveryStatus()
                != AuditOutboxEntity.DeliveryStatus.PENDING) {
            return;
        }

        event.setAttempts(event.getAttempts() + 1);
        event.setLastError(truncate(getErrorMessage(error)));

        if (event.getAttempts() >= MAX_ATTEMPTS) {
            event.setDeliveryStatus(AuditOutboxEntity.DeliveryStatus.FAILED);
        }

        auditOutboxRepository.save(event);
    }

    private String getErrorMessage(RuntimeException error) {
        String message = error.getMessage();

        if (message == null || message.isBlank()) {
            return error.getClass().getSimpleName();
        }

        return message;
    }

    private String truncate(String value) {
        return value.length() <= MAX_ERROR_LENGTH
                ? value
                : value.substring(0, MAX_ERROR_LENGTH);
    }
}