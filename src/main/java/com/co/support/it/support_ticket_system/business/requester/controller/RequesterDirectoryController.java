package com.co.support.it.support_ticket_system.business.requester.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.REQUESTER_FROM_DIRECTORY;

/**
 * Alta de solicitantes desde el directorio interno de empleados.
 */
@Tag(name = "Solicitantes")
public interface RequesterDirectoryController {

    @Operation(summary = "Crear solicitante desde el directorio de empleados", description = "Consulta el directorio interno de empleados (servicio sin seguridad) y crea el solicitante con su nombre, correo y teléfono. 404 si el empleado no existe; 409 si el correo ya está registrado; 503 si el directorio no responde.")
    @PostMapping(REQUESTER_FROM_DIRECTORY)
    ResponseEntity<CustomResponse<RequesterResponseDto>> createFromDirectory(@PathVariable("employeeId") Long employeeId);
}
