package com.co.support.it.support_ticket_system.business.ticket.controller.impl;

import com.co.support.it.support_ticket_system.business.ticket.controller.TicketCommentController;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.service.TicketCommentService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKETS;
import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKET_COMMENTS;

@RestController
@RequestMapping(TICKET_COMMENTS)
public class TicketCommentControllerImpl extends GenericControllerImpl implements TicketCommentController {

    private final TicketCommentService ticketCommentService;

    public TicketCommentControllerImpl(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @Override
    public ResponseEntity<CustomResponse<List<TicketCommentResponseDto>>> findByTicketId(Long ticketId) {
        return returnList(ticketCommentService.findByTicketId(ticketId));
    }

    @Override
    public ResponseEntity<CustomResponse<TicketCommentResponseDto>> save(
            Long ticketId,
            TicketCommentRequestDto request
    ) {
        TicketCommentResponseDto saved = ticketCommentService.save(ticketId, request);
        return returnCreated(TICKETS + "/" + ticketId + "/comments/" + saved.id(), saved);
    }
}
