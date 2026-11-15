package com.co.support.it.support_ticket_system.business.batch.job;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventClassifierTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final OffsetDateTime exportedAt = OffsetDateTime.of(2026, 11, 16, 15, 0, 0, 0, ZoneOffset.UTC);

    private AuditEventRecord event(String type, String payload) {
        return new AuditEventRecord("e-1", "support-ticket-system", type, "TICKET", "7",
                Instant.parse("2026-11-16T14:05:00Z"), "jcastaneda", "127.0.0.1", "corr-1", payload);
    }

    @Test
    void httpRequest_extractsMethodPathStatusAndDuration() {
        AuditHistoryRecord record = AuditEventClassifier.classify(event("API_REQUEST_COMPLETED",
                "{\"method\":\"POST\",\"path\":\"/api/v1/tickets\",\"statusCode\":201,\"durationMillis\":42,\"response\":{\"id\":7}}"),
                5L, jsonMapper, exportedAt);

        assertThat(record.eventCategory()).isEqualTo(AuditEventClassifier.HTTP_REQUEST);
        assertThat(record.httpMethod()).isEqualTo("POST");
        assertThat(record.httpPath()).isEqualTo("/api/v1/tickets");
        assertThat(record.httpStatus()).isEqualTo(201);
        assertThat(record.durationMillis()).isEqualTo(42L);
        assertThat(record.ticketNumber()).isNull();
        assertThat(record.jobExecutionId()).isEqualTo(5L);
        assertThat(record.occurredAtUtc()).isEqualTo(OffsetDateTime.parse("2026-11-16T14:05:00Z"));
    }

    @Test
    void statusChange_extractsTicketAndBothStatuses() {
        AuditHistoryRecord record = AuditEventClassifier.classify(event("TICKET_STATUS_CHANGED",
                "{\"ticketNumber\":\"TCK-2026-000007\",\"previousStatus\":\"IN_PROGRESS\",\"newStatus\":\"RESOLVED\"}"),
                5L, jsonMapper, exportedAt);

        assertThat(record.eventCategory()).isEqualTo(AuditEventClassifier.BUSINESS_EVENT);
        assertThat(record.ticketNumber()).isEqualTo("TCK-2026-000007");
        assertThat(record.previousStatus()).isEqualTo("IN_PROGRESS");
        assertThat(record.newStatus()).isEqualTo("RESOLVED");
        assertThat(record.httpMethod()).isNull();
    }

    @Test
    void ticketCreated_usesStatusAsTheNewStatus() {
        AuditHistoryRecord record = AuditEventClassifier.classify(event("TICKET_CREATED",
                "{\"ticketNumber\":\"TCK-2026-000008\",\"status\":\"OPEN\"}"), 5L, jsonMapper, exportedAt);

        assertThat(record.newStatus()).isEqualTo("OPEN");
        assertThat(record.previousStatus()).isNull();
    }

    @Test
    void invalidPayload_isCopiedWithoutExtractedData() {
        AuditHistoryRecord record = AuditEventClassifier.classify(event("API_REQUEST_COMPLETED", "{no es json"),
                5L, jsonMapper, exportedAt);

        assertThat(record.eventId()).isEqualTo("e-1");
        assertThat(record.httpMethod()).isNull();
        assertThat(record.httpStatus()).isNull();
    }
}
