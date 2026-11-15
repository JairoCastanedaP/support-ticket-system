package com.co.support.it.support_ticket_system.business.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de las notificaciones por correo. El destinatario llega por TICKET_ADMIN_EMAIL.
 */
@ConfigurationProperties(prefix = "app.notifications")
public record NotificationProperties(
        boolean enabled,
        String adminEmail,
        String from,
        int intervalSeconds,
        int maxAttempts
) {
}
