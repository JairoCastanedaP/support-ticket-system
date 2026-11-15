package com.co.support.it.support_ticket_system.infrastructure.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.PORTAL;

/**
 * Aplica la validación de API key solo a las rutas del portal.
 */
@Configuration
public class PortalSecurityWebConfiguration implements WebMvcConfigurer {

    private final PortalApiKeyInterceptor portalApiKeyInterceptor;

    public PortalSecurityWebConfiguration(PortalApiKeyInterceptor portalApiKeyInterceptor) {
        this.portalApiKeyInterceptor = portalApiKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(portalApiKeyInterceptor)
                .addPathPatterns(PORTAL + "/**");
    }
}
