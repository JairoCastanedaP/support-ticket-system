package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.service.TicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Optional<TicketResponseDto> ticketOptional = ticketService.findById(id);
        if(ticketOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketOptional.get());
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

        Optional<TicketResponseDto> updatedTicket= ticketService.update(id,ticketRequestDto);
        if(updatedTicket.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTicket.get());
    }
}
