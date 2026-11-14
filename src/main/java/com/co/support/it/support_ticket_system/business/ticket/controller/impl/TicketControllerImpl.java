package com.co.support.it.support_ticket_system.business.ticket.controller.impl;

import com.co.support.it.support_ticket_system.business.ticket.controller.TicketController;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.service.TicketService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKETS;

@Slf4j
@RestController
@RequestMapping(TICKETS)
public class TicketControllerImpl extends GenericControllerImpl implements TicketController {

    private final TicketService ticketService;

    public TicketControllerImpl(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public ResponseEntity<CustomResponse<PageResponse<TicketResponseDto>>> findAll(
            TicketStatus status,
            TicketPriority priority,
            Long categoryId,
            Long supportAgentId,
            Pageable pageable
    ) {
        return returnPage(ticketService.findAll(status, priority, categoryId, supportAgentId, pageable));
    }

    @Override
    public ResponseEntity<CustomResponse<TicketResponseDto>> findById(Long id) {
        return returnOk(ticketService.findById(id));
    }

    @Override
    public ResponseEntity<CustomResponse<TicketResponseDto>> save(TicketRequestDto request) {
        TicketResponseDto saved = ticketService.save(request);
        return returnCreated(TICKETS + "/" + saved.id(), saved);
    }

    @Override
    public ResponseEntity<CustomResponse<TicketResponseDto>> update(Long id, TicketRequestDto request) {
        log.info("Actualizando ticket con id: {}", id);
        return returnUpdated(ticketService.update(id, request));
    }

    @Override
    public ResponseEntity<CustomResponse<TicketResponseDto>> changeStatus(Long id, TicketStatusChangeRequestDto request) {
        log.info("Cambiando estado del ticket {} a {}", id, request.status());
        return returnUpdated(ticketService.changeStatus(id, request));
    }

    @Override
    public ResponseEntity<CustomResponse<Void>> delete(Long id) {
        ticketService.delete(id);
        return returnDeleted();
    }

    @Override
    public ResponseEntity<CustomResponse<List<TicketStatusHistoryResponseDto>>> findStatusHistory(Long id) {
        return returnList(ticketService.findStatusHistory(id));
    }
}
