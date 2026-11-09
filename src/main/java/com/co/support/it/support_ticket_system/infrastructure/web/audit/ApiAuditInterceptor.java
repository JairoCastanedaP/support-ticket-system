package com.co.support.it.support_ticket_system.infrastructure.web.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class ApiAuditInterceptor implements HandlerInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger(ApiAuditInterceptor.class);

    private static final Pattern VALID_CORRELATION_ID =
            Pattern.compile("[A-Za-z0-9._:-]{1,100}");

    private final ApiAccessAuditService apiAccessAuditService;

    public ApiAuditInterceptor(ApiAccessAuditService apiAccessAuditService) {
        this.apiAccessAuditService = apiAccessAuditService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String correlationId = request.getHeader("X-Correlation-ID");

        if (correlationId == null
                || !VALID_CORRELATION_ID.matcher(correlationId).matches()) {
            correlationId = UUID.randomUUID().toString();
        }

        ApiAuditContext context = new ApiAuditContext();
        context.setCorrelationId(correlationId);
        context.setStartedAtNanos(System.nanoTime());

        request.setAttribute(ApiAuditContext.ATTRIBUTE, context);
        response.setHeader("X-Correlation-ID", correlationId);

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        Object attribute = request.getAttribute(ApiAuditContext.ATTRIBUTE);

        if (!(attribute instanceof ApiAuditContext context)) {
            return;
        }

        long durationMillis =
                (System.nanoTime() - context.getStartedAtNanos()) / 1_000_000;

        String exceptionType = exception == null
                ? null
                : exception.getClass().getSimpleName();

        try {
            apiAccessAuditService.recordCompletedRequest(
                    context.getCorrelationId(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMillis,
                    request.getRemoteAddr(),
                    context.getRequestBody(),
                    context.getResponseBody(),
                    exceptionType
            );
        } catch (RuntimeException auditException) {
            logger.error(
                    "No se pudo guardar la auditoría HTTP. correlationId={}",
                    context.getCorrelationId(),
                    auditException
            );
        }
    }
}