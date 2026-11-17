package com.co.support.it.support_ticket_system.business.portal.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.portal.dto.PortalTicketResponseDto;
import com.co.support.it.support_ticket_system.business.shared.validation.ValidTicketNumber;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.BY_TICKET_NUMBER;

/**
 * Portal de clientes externos. Requiere el header X-API-Key (ver PortalApiKeyInterceptor).
 */
@Tag(name = "Portal de clientes", description = "Consulta para clientes externos protegida con el header X-API-Key. Respuesta reducida, sin datos personales ni internos.")
@SecurityRequirement(name = "portalApiKey")
public interface PortalTicketController {

    @Operation(summary = "Consultar ticket desde el portal", description = "401 sin header X-API-Key o con clave incorrecta; 400 si el número no tiene el formato TCK-AAAA-NNNNNN; 404 si no existe.")
    @GetMapping(BY_TICKET_NUMBER)
    ResponseEntity<CustomResponse<PortalTicketResponseDto>> findByTicketNumber(
            @PathVariable("ticketNumber") @ValidTicketNumber String ticketNumber
    );
}
