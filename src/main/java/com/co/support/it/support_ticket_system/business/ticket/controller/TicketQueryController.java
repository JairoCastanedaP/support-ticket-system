package com.co.support.it.support_ticket_system.business.ticket.controller;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.*;

/**
 * Consultas de solo lectura sobre tickets.
 */
public interface TicketQueryController {

    @GetMapping(TICKET_BY_NUMBER)
    ResponseEntity<CustomResponse<TicketResponseDto>> findByTicketNumber(
            @PathVariable("ticketNumber") String ticketNumber
    );

    @GetMapping(TICKET_SEARCH)
    ResponseEntity<CustomResponse<List<TicketSearchResultDto>>> search(@RequestParam("text") String text);

    @GetMapping(TICKET_SUMMARY)
    ResponseEntity<CustomResponse<PageResponse<TicketSummaryResponseDto>>> findSummary(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @PageableDefault(size = 20, sort = "ticketId", direction = Sort.Direction.DESC) Pageable pageable
    );

    @GetMapping(TICKET_INDICATORS)
    ResponseEntity<CustomResponse<TicketIndicatorsResponseDto>> getIndicators();
}
