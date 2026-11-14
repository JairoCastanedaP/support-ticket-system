package com.co.support.it.support_ticket_system.business.shared.config.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Habilita la auditoría JPA. El actor se toma del header {@value #USER_HEADER};
 * si no llega, no es válido o la operación no viene de una petición HTTP, se registra {@value #SYSTEM_ACTOR}.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    public static final String USER_HEADER = "X-User-Id";
    public static final String SYSTEM_ACTOR = "SYSTEM";

    private static final Pattern VALID_USER = Pattern.compile("[A-Za-z0-9._@-]{1,50}");

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(currentActor());
    }

    private String currentActor() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return SYSTEM_ACTOR;
        }

        HttpServletRequest request = attributes.getRequest();
        String user = request.getHeader(USER_HEADER);

        return user != null && VALID_USER.matcher(user).matches() ? user : SYSTEM_ACTOR;
    }
}
