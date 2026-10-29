package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketCommentResponseDto;
import com.co.support.it.support_ticket_system.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/comments")
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    public TicketCommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @GetMapping
    public ResponseEntity<List<TicketCommentResponseDto>> findByTicketId(
            @PathVariable Long ticketId
    ) {
        return ResponseEntity.ok(ticketCommentService.findByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketCommentResponseDto> save(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketCommentRequestDto requestDto
    ) {
        TicketCommentResponseDto savedComment =
                ticketCommentService.save(ticketId, requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedComment);
    }
}