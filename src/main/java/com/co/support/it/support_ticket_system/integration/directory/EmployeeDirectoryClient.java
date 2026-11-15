package com.co.support.it.support_ticket_system.integration.directory;

import com.co.support.it.support_ticket_system.generic.exception.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Optional;

/**
 * Cliente del directorio interno de empleados. Es un servicio de la red interna y no requiere seguridad.
 */
@Component
public class EmployeeDirectoryClient {

    private final RestClient restClient;

    public EmployeeDirectoryClient(@Qualifier("employeeDirectoryRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<EmployeeDto> findById(Long employeeId) {
        try {
            DirectoryUser user = restClient.get()
                    .uri("/users/{id}", employeeId)
                    .retrieve()
                    .body(DirectoryUser.class);

            return Optional.ofNullable(user).filter(u -> u.id() != null).map(EmployeeDirectoryClient::toEmployee);
        } catch (HttpClientErrorException.NotFound notFound) {
            return Optional.empty();
        } catch (RestClientException exception) {
            throw new ExternalServiceException("El directorio de empleados no está disponible en este momento", exception);
        }
    }

    /**
     * Separa nombre y apellido (sin títulos como "Mrs.") y deja el teléfono solo con dígitos, sin extensión.
     */
    static EmployeeDto toEmployee(DirectoryUser user) {
        String[] parts = Arrays.stream(user.name().trim().split("\\s+"))
                .filter(part -> !part.endsWith("."))
                .toArray(String[]::new);

        String name = parts.length > 0 ? parts[0] : user.name();
        String surname = parts.length > 1 ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)) : "-";

        return new EmployeeDto(user.id(), name, surname, user.email(), normalizePhone(user.phone()));
    }

    static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String withoutExtension = phone.split("(?i)\\s*x")[0];
        String digits = withoutExtension.replaceAll("\\D", "");
        return digits.length() >= 7 && digits.length() <= 15 ? digits : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DirectoryUser(Long id, String name, String email, String phone) {
    }
}
