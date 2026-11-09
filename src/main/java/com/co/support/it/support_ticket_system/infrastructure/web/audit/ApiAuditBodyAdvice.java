package com.co.support.it.support_ticket_system.infrastructure.web.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@ControllerAdvice
public class ApiAuditBodyAdvice
        extends RequestBodyAdviceAdapter
        implements ResponseBodyAdvice<Object> {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "token",
            "secret",
            "authorization",
            "email",
            "phone",
            "description",
            "comment"
    );

    private final JsonMapper jsonMapper;

    public ApiAuditBodyAdvice(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        captureBody(body, true);
        return body;
    }

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        captureBody(body, false);
        return body;
    }

    private void captureBody(Object body, boolean requestBody) {
        HttpServletRequest request = currentRequest();

        if (request == null || body == null) {
            return;
        }

        ApiAuditContext context = (ApiAuditContext) request.getAttribute(
                ApiAuditContext.ATTRIBUTE
        );

        if (context == null) {
            context = new ApiAuditContext();
            request.setAttribute(ApiAuditContext.ATTRIBUTE, context);
        }

        JsonNode snapshot = jsonMapper.valueToTree(body);
        removeSensitiveFields(snapshot);

        if (requestBody) {
            context.setRequestBody(snapshot);
        } else {
            context.setResponseBody(snapshot);
        }
    }

    private void removeSensitiveFields(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            List<String> fieldNames = new ArrayList<>(objectNode.propertyNames());

            for (String fieldName : fieldNames) {
                String normalizedName =
                        fieldName.toLowerCase(Locale.ROOT);

                boolean sensitive = SENSITIVE_FIELDS.stream()
                        .anyMatch(normalizedName::contains);

                if (sensitive) {
                    objectNode.remove(fieldName);
                } else {
                    removeSensitiveFields(objectNode.get(fieldName));
                }
            }
        } else if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                removeSensitiveFields(item);
            }
        }
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }

        return null;
    }
}