package com.co.support.it.support_ticket_system.integration.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

/**
 * Un {@link RestClient} por servicio externo, con timeouts cortos para no bloquear la API.
 */
@Configuration
@EnableConfigurationProperties(IntegrationProperties.class)
public class IntegrationClientsConfiguration {

    @Bean
    public RestClient assetCatalogRestClient(IntegrationProperties properties) {
        return build(properties, properties.assets().baseUrl());
    }

    @Bean
    public RestClient employeeDirectoryRestClient(IntegrationProperties properties) {
        return build(properties, properties.directory().baseUrl());
    }

    private RestClient build(IntegrationProperties properties, String baseUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
