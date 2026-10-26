package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.service.TicketService;
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
    public ResponseEntity<List<TicketEntity>> findAll(){
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketEntity> findById(@PathVariable Long id){
        Optional<TicketEntity> ticketOptional = ticketService.findById(id);
        if(ticketOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketOptional.get());
    }

    @PostMapping
    public ResponseEntity<TicketEntity> save(@RequestBody TicketEntity ticket){
        ticket.setId(null);
        log.info("Saving Ticket: {}",ticket);
        TicketEntity savedTicket= ticketService.save(ticket);
        URI location = URI.create("/api/v1/tickets/" + savedTicket.getId());
        return ResponseEntity.created(location).body(savedTicket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketEntity> update(
            @PathVariable Long id,
            @RequestBody TicketEntity ticket

    ){
        log.info("Saving new ticket");


        Optional<TicketEntity> updatedTicket= ticketService.update(id,ticket);
        if(updatedTicket.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedTicket.get());
    }

}
