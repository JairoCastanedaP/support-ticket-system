package com.co.support.it.support_ticket_system.infrastructure.web.security;

import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Protege el portal de clientes externos: la petición debe traer el header {@value #API_KEY_HEADER}
 * con la clave configurada en PORTAL_API_KEY. Si falta o no coincide, responde 401 sin llegar al controller.
 */
@Slf4j
@Component
public class PortalApiKeyInterceptor implements HandlerInterceptor {

    public static final String API_KEY_HEADER = "X-API-Key";

    private final byte[] expectedKey;
    private final JsonMapper jsonMapper;

    public PortalApiKeyInterceptor(@Value("${app.portal.api-key:}") String apiKey, JsonMapper jsonMapper) {
        this.expectedKey = apiKey == null ? new byte[0] : apiKey.getBytes(StandardCharsets.UTF_8);
        this.jsonMapper = jsonMapper;
        if (expectedKey.length == 0) {
            log.warn("PORTAL_API_KEY no está configurada: el portal rechazará todas las peticiones");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String providedKey = request.getHeader(API_KEY_HEADER);

        if (providedKey == null || providedKey.isBlank()) {
            return reject(response, "Falta el header " + API_KEY_HEADER);
        }
        // Comparación en tiempo constante para no revelar la clave por diferencias de tiempo.
        if (expectedKey.length == 0
                || !MessageDigest.isEqual(expectedKey, providedKey.getBytes(StandardCharsets.UTF_8))) {
            return reject(response, "La API key no es válida");
        }
        return true;
    }

    private boolean reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(jsonMapper.writeValueAsString(CustomResponse.error(message, null)));
        return false;
    }
}
