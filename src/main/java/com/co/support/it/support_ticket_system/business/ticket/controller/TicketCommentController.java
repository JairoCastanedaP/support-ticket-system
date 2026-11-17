package com.co.support.it.support_ticket_system.business.ticket.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Comentarios", description = "Comentarios de seguimiento de un ticket.")
public interface TicketCommentController {

    @Operation(summary = "Listar comentarios del ticket", description = "404 si el ticket no existe.")
    @GetMapping
    ResponseEntity<CustomResponse<List<TicketCommentResponseDto>>> findByTicketId(
            @PathVariable("ticketId") Long ticketId
    );

    @Operation(summary = "Agregar comentario", description = "404 si el ticket no existe; 400 si el comentario está vacío.")
    @PostMapping
    ResponseEntity<CustomResponse<TicketCommentResponseDto>> save(
            @PathVariable("ticketId") Long ticketId,
            @Valid @RequestBody TicketCommentRequestDto request
    );
}
