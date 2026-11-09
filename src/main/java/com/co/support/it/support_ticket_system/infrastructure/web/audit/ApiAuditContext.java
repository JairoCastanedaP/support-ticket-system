package com.co.support.it.support_ticket_system.infrastructure.web.audit;

import tools.jackson.databind.JsonNode;

public class ApiAuditContext {

    public static final String ATTRIBUTE =
            ApiAuditContext.class.getName();

    private JsonNode requestBody;
    private JsonNode responseBody;
    private String correlationId;
    private long startedAtNanos;

    public JsonNode getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(JsonNode requestBody) {
        this.requestBody = requestBody;
    }

    public JsonNode getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(JsonNode responseBody) {
        this.responseBody = responseBody;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public long getStartedAtNanos() {
        return startedAtNanos;
    }

    public void setStartedAtNanos(long startedAtNanos) {
        this.startedAtNanos = startedAtNanos;
    }
}