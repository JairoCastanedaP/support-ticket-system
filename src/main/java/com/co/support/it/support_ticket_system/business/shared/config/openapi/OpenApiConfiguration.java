package com.co.support.it.support_ticket_system.business.shared.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.co.support.it.support_ticket_system.business.shared.config.audit.JpaAuditingConfig.USER_HEADER;
import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.PORTAL;
import static com.co.support.it.support_ticket_system.infrastructure.web.security.PortalApiKeyInterceptor.API_KEY_HEADER;

/**
 * Documentación OpenAPI (Swagger UI en /swagger-ui.html).
 */
@Configuration
public class OpenApiConfiguration {

    public static final String PORTAL_SECURITY_SCHEME = "portalApiKey";

    private static final String DESCRIPTION = """
            API REST para registrar, atender y dar seguimiento a tickets de soporte de TI.

            **Bases de datos:** MySQL (operativa), Oracle (auditoría central, alimentada por una bandeja de salida) \
            y PostgreSQL (histórico de auditoría, alimentado con Spring Batch).

            **Formato de respuesta:** todas las respuestas usan `CustomResponse` con `status` (SUCCESS, EMPTY o ERROR), \
            `message`, `timestamp` y `data`.

            **Auditoría:** el header opcional `X-User-Id` identifica al usuario en las operaciones de escritura; \
            si no se envía, se registra `SYSTEM`.

            **Portal de clientes:** requiere el header `X-API-Key` (botón *Authorize*).
            """;

    @Bean
    public OpenAPI supportTicketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Support Ticket System API")
                        .version("v1")
                        .description(DESCRIPTION)
                        .contact(new Contact().name("Jairo Alejandro Castañeda Pedraza")))
                .components(new Components()
                        .addSecuritySchemes(PORTAL_SECURITY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(API_KEY_HEADER)
                                .description("Clave del portal de clientes (variable PORTAL_API_KEY)")));
    }

    /**
     * Agrega el header opcional X-User-Id a las operaciones de escritura (fuera del portal).
     */
    @Bean
    public OpenApiCustomizer userHeaderCustomizer() {
        Set<PathItem.HttpMethod> writes = Set.of(
                PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT, PathItem.HttpMethod.PATCH, PathItem.HttpMethod.DELETE);

        return openApi -> openApi.getPaths().forEach((path, item) -> {
            if (path.startsWith(PORTAL)) {
                return;
            }
            item.readOperationsMap().forEach((method, operation) -> {
                if (writes.contains(method)) {
                    operation.addParametersItem(new HeaderParameter()
                            .name(USER_HEADER)
                            .required(false)
                            .description("Usuario que ejecuta la operación (auditoría). Si no se envía, se registra SYSTEM.")
                            .schema(new StringSchema().example("jcastaneda")));
                }
            });
        });
    }
}
