package com.co.support.it.support_ticket_system.business.portal.controller;

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
public interface PortalTicketController {

    @GetMapping(BY_TICKET_NUMBER)
    ResponseEntity<CustomResponse<PortalTicketResponseDto>> findByTicketNumber(
            @PathVariable("ticketNumber") @ValidTicketNumber String ticketNumber
    );
}
