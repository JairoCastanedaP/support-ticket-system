package com.co.support.it.support_ticket_system.integration.asset;

import com.co.support.it.support_ticket_system.generic.exception.ExternalServiceException;
import com.co.support.it.support_ticket_system.integration.config.IntegrationProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Cliente del catálogo de activos de TI (servicio externo protegido con token).
 * Hace login una vez, guarda el token en memoria y lo envía como Bearer en cada consulta.
 * Si el token vence (401), vuelve a hacer login y reintenta una sola vez.
 */
@Slf4j
@Component
public class AssetCatalogClient {

    private static final String SERVICE_UNAVAILABLE = "El catálogo de activos de TI no está disponible en este momento";

    private final RestClient restClient;
    private final IntegrationProperties.Assets properties;
    private volatile String accessToken;

    public AssetCatalogClient(
            @Qualifier("assetCatalogRestClient") RestClient restClient,
            IntegrationProperties properties
    ) {
        this.restClient = restClient;
        this.properties = properties.assets();
    }

    /**
     * Consulta un activo por id. Vacío si el proveedor no lo encuentra.
     */
    public Optional<AssetResponseDto> findById(Long id) {
        try {
            return Optional.of(toAsset(getProduct(id)));
        } catch (HttpClientErrorException.Unauthorized expired) {
            log.info("El token del catálogo de activos fue rechazado; se renueva");
            accessToken = null;
            return findByIdWithoutRetry(id);
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest notFound) {
            // El proveedor responde 400 cuando el id no existe.
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new ExternalServiceException(SERVICE_UNAVAILABLE, exception);
        }
    }

    private Optional<AssetResponseDto> findByIdWithoutRetry(Long id) {
        try {
            return Optional.of(toAsset(getProduct(id)));
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest notFound) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new ExternalServiceException(SERVICE_UNAVAILABLE, exception);
        }
    }

    private ProductResponse getProduct(Long id) {
        return restClient.get()
                .uri("/products/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token())
                .retrieve()
                .body(ProductResponse.class);
    }

    private synchronized String token() {
        if (accessToken == null) {
            accessToken = login();
        }
        return accessToken;
    }

    private String login() {
        try {
            LoginResponse response = restClient.post()
                    .uri("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LoginRequest(properties.email(), properties.password()))
                    .retrieve()
                    .body(LoginResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new ExternalServiceException("El catálogo de activos no entregó un token de acceso");
            }
            return response.accessToken();
        } catch (HttpClientErrorException exception) {
            log.error("Login rechazado por el catálogo de activos ({}). Revise ASSETS_API_EMAIL y ASSETS_API_PASSWORD",
                    exception.getStatusCode().value());
            throw new ExternalServiceException(SERVICE_UNAVAILABLE, exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException(SERVICE_UNAVAILABLE, exception);
        }
    }

    private AssetResponseDto toAsset(ProductResponse product) {
        if (product == null) {
            throw new ExternalServiceException(SERVICE_UNAVAILABLE);
        }
        return new AssetResponseDto(
                product.id(),
                product.title(),
                product.description(),
                product.category() == null ? null : product.category().name(),
                product.price()
        );
    }

    private record LoginRequest(String email, String password) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record LoginResponse(@JsonProperty("access_token") String accessToken) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductResponse(Long id, String title, String description, BigDecimal price, Category category) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Category(String name) {
    }
}
