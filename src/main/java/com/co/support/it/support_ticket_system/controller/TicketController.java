package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.service.TicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.co.support.it.support_ticket_system.dto.TicketStatusHistoryResponseDto;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService= ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> findAll(){
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> save(@Valid @RequestBody TicketRequestDto ticket){
        log.info("Saving Ticket: {}",ticket);
        TicketResponseDto savedTicket= ticketService.save(ticket);
        URI location = URI.create("/api/v1/tickets/" + savedTicket.id());
        return ResponseEntity.created(location).body(savedTicket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDto ticketRequestDto
    ){
        log.info("Updating ticket with id: {}", id);

        return ResponseEntity.ok(
                ticketService.update(id,ticketRequestDto)
        );
    }
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<TicketStatusHistoryResponseDto>> findStatusHistory(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ticketService.findStatusHistory(id));
    }
}
