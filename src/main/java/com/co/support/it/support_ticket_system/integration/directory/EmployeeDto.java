package com.co.support.it.support_ticket_system.integration.directory;

/**
 * Empleado del directorio interno, con los datos ya normalizados para crear un solicitante.
 */
public record EmployeeDto(Long id, String name, String surname, String email, String phoneNumber) {
}
