package com.co.support.it.support_ticket_system.infrastructure.web.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiAuditWebConfiguration implements WebMvcConfigurer {

    private final ApiAuditInterceptor apiAuditInterceptor;

    public ApiAuditWebConfiguration(ApiAuditInterceptor apiAuditInterceptor) {
        this.apiAuditInterceptor = apiAuditInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAuditInterceptor)
                .addPathPatterns("/api/**");
    }
}