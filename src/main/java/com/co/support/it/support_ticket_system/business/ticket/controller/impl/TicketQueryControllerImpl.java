package com.co.support.it.support_ticket_system.business.ticket.controller.impl;

import com.co.support.it.support_ticket_system.business.ticket.controller.TicketQueryController;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.service.TicketQueryService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKETS;

@RestController
@RequestMapping(TICKETS)
public class TicketQueryControllerImpl extends GenericControllerImpl implements TicketQueryController {

    private final TicketQueryService ticketQueryService;

    public TicketQueryControllerImpl(TicketQueryService ticketQueryService) {
        this.ticketQueryService = ticketQueryService;
    }

    @Override
    public ResponseEntity<CustomResponse<TicketResponseDto>> findByTicketNumber(String ticketNumber) {
        return returnOk(ticketQueryService.findByTicketNumber(ticketNumber));
    }

    @Override
    public ResponseEntity<CustomResponse<List<TicketSearchResultDto>>> search(String text) {
        return returnList(ticketQueryService.search(text));
    }

    @Override
    public ResponseEntity<CustomResponse<PageResponse<TicketSummaryResponseDto>>> findSummary(
            TicketStatus status, TicketPriority priority, Pageable pageable) {
        return returnPage(ticketQueryService.findSummary(status, priority, pageable));
    }

    @Override
    public ResponseEntity<CustomResponse<TicketIndicatorsResponseDto>> getIndicators() {
        return returnOk(ticketQueryService.getIndicators());
    }
}
