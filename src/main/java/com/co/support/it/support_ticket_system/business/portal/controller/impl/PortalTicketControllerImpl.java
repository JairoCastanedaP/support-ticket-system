package com.co.support.it.support_ticket_system.business.portal.controller.impl;

import com.co.support.it.support_ticket_system.business.portal.controller.PortalTicketController;
import com.co.support.it.support_ticket_system.business.portal.dto.PortalTicketResponseDto;
import com.co.support.it.support_ticket_system.business.portal.service.PortalTicketService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.PORTAL_TICKETS;

@RestController
@RequestMapping(PORTAL_TICKETS)
public class PortalTicketControllerImpl extends GenericControllerImpl implements PortalTicketController {

    private final PortalTicketService portalTicketService;

    public PortalTicketControllerImpl(PortalTicketService portalTicketService) {
        this.portalTicketService = portalTicketService;
    }

    @Override
    public ResponseEntity<CustomResponse<PortalTicketResponseDto>> findByTicketNumber(String ticketNumber) {
        return returnOk(portalTicketService.findByTicketNumber(ticketNumber));
    }
}
