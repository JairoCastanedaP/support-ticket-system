package com.co.support.it.support_ticket_system.business.ticket.controller;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TicketCommentController {

    @GetMapping
    ResponseEntity<CustomResponse<List<TicketCommentResponseDto>>> findByTicketId(
            @PathVariable("ticketId") Long ticketId
    );

    @PostMapping
    ResponseEntity<CustomResponse<TicketCommentResponseDto>> save(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody TicketCommentRequestDto request
    );
}
