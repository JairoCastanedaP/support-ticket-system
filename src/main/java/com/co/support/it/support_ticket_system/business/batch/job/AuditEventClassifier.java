package com.co.support.it.support_ticket_system.business.batch.job;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Transformación del batch: clasifica el evento y extrae del JSON los datos útiles para consultar
 * el histórico sin leer el payload completo (que además se descarta para no copiar datos innecesarios).
 */
public final class AuditEventClassifier {

    public static final String HTTP_REQUEST = "HTTP_REQUEST";
    public static final String BUSINESS_EVENT = "BUSINESS_EVENT";

    private static final String API_REQUEST_COMPLETED = "API_REQUEST_COMPLETED";
    private static final int MAX_PATH_LENGTH = 300;

    private AuditEventClassifier() {
    }

    public static AuditHistoryRecord classify(AuditEventRecord event, Long jobExecutionId, JsonMapper jsonMapper,
                                              OffsetDateTime exportedAt) {
        JsonNode payload = parse(event.payload(), jsonMapper);
        boolean httpRequest = API_REQUEST_COMPLETED.equals(event.eventType());

        return new AuditHistoryRecord(
                event.eventId(),
                event.sourceSystem(),
                event.eventType(),
                httpRequest ? HTTP_REQUEST : BUSINESS_EVENT,
                event.entityType(),
                event.entityId(),
                event.occurredAtUtc().atOffset(ZoneOffset.UTC),
                event.actorId(),
                event.clientIp(),
                event.correlationId(),
                httpRequest ? text(payload, "method") : null,
                httpRequest ? truncate(text(payload, "path")) : null,
                httpRequest ? integer(payload, "statusCode") : null,
                httpRequest ? longValue(payload, "durationMillis") : null,
                httpRequest ? null : text(payload, "ticketNumber"),
                httpRequest ? null : text(payload, "previousStatus"),
                httpRequest ? null : firstText(payload, "newStatus", "status"),
                exportedAt,
                jobExecutionId
        );
    }

    private static JsonNode parse(String payload, JsonMapper jsonMapper) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        try {
            return jsonMapper.readTree(payload);
        } catch (RuntimeException invalidJson) {
            // Un payload ilegible no detiene el volcado: el evento se copia sin los datos extraídos.
            return null;
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? null : value.asString();
    }

    private static String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static Integer integer(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || !value.isNumber() ? null : value.asInt();
    }

    private static Long longValue(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || !value.isNumber() ? null : value.asLong();
    }

    private static String truncate(String value) {
        return value == null || value.length() <= MAX_PATH_LENGTH ? value : value.substring(0, MAX_PATH_LENGTH);
    }
}
