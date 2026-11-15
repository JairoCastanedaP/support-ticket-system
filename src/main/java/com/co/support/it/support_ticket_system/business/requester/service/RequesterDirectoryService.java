package com.co.support.it.support_ticket_system.business.requester.service;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.integration.directory.EmployeeDirectoryClient;
import com.co.support.it.support_ticket_system.integration.directory.EmployeeDto;
import org.springframework.stereotype.Service;

/**
 * Crea solicitantes a partir del directorio interno de empleados.
 * Reutiliza el servicio de solicitantes, así se aplica la misma regla de correo único (409).
 */
@Service
public class RequesterDirectoryService {

    private final EmployeeDirectoryClient directoryClient;
    private final RequesterService requesterService;

    public RequesterDirectoryService(EmployeeDirectoryClient directoryClient, RequesterService requesterService) {
        this.directoryClient = directoryClient;
        this.requesterService = requesterService;
    }

    public RequesterResponseDto createFromDirectory(Long employeeId) {
        EmployeeDto employee = directoryClient.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró el empleado con id " + employeeId + " en el directorio"));

        return requesterService.save(new RequesterRequestDto(
                employee.name(),
                employee.surname(),
                employee.email(),
                employee.phoneNumber()
        ));
    }
}
