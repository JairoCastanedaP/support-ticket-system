package com.co.support.it.support_ticket_system.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuración de los servicios externos. Las credenciales llegan por variables de entorno.
 */
@ConfigurationProperties(prefix = "app.integrations")
public record IntegrationProperties(
        Duration connectTimeout,
        Duration readTimeout,
        Assets assets,
        Directory directory
) {

    /** Catálogo de activos de TI: requiere login y token Bearer. */
    public record Assets(String baseUrl, String email, String password) {
    }

    /** Directorio interno de empleados: sin seguridad. */
    public record Directory(String baseUrl) {
    }
}
