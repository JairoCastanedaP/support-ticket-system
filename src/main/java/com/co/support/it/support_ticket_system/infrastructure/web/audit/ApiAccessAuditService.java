package com.co.support.it.support_ticket_system.infrastructure.web.audit;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository.AuditOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Service
public class ApiAccessAuditService {

    private final AuditOutboxRepository auditOutboxRepository;
    private final JsonMapper jsonMapper;

    public ApiAccessAuditService(
            AuditOutboxRepository auditOutboxRepository,
            JsonMapper jsonMapper
    ) {
        this.auditOutboxRepository = auditOutboxRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCompletedRequest(
            String correlationId,
            String method,
            String path,
            int statusCode,
            long durationMillis,
            String clientIp,
            JsonNode requestBody,
            JsonNode responseBody,
            String exceptionType
    ) {
        HttpRequestAuditPayload payload = new HttpRequestAuditPayload(
                method,
                path,
                statusCode,
                durationMillis,
                requestBody,
                responseBody,
                exceptionType
        );

        AuditOutboxEntity event = new AuditOutboxEntity();
        event.setSourceSystem("support-ticket-system");
        event.setEventType("API_REQUEST_COMPLETED");
        event.setEntityType("HTTP_REQUEST");
        event.setEntityId(correlationId);
        event.setOccurredAtUtc(Instant.now());
        event.setClientIp(clientIp);
        event.setCorrelationId(correlationId);
        event.setPayload(jsonMapper.writeValueAsString(payload));

        auditOutboxRepository.save(event);
    }

    private record HttpRequestAuditPayload(
            String method,
            String path,
            int statusCode,
            long durationMillis,
            JsonNode request,
            JsonNode response,
            String exceptionType
    ) {
    }
}