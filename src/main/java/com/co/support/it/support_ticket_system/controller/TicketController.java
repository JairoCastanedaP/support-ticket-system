package com.co.support.it.support_ticket_system.controller;

import com.co.support.it.support_ticket_system.entity.TicketEntity;
import com.co.support.it.support_ticket_system.repository.TicketRepository;
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

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository){
        this.ticketRepository= ticketRepository;
    }

    @GetMapping
    public ResponseEntity<List<TicketEntity>> findAll(){
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketEntity> findById(@PathVariable Long id){
        Optional<TicketEntity> ticketOptional= ticketRepository.findById(id);
        if(ticketOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticketOptional.get());
    }

    @PostMapping
    public ResponseEntity<TicketEntity> save(@RequestBody TicketEntity ticket){
        ticket.setId(null);
        log.info("Saving Ticket: {}",ticket);
        TicketEntity savedTicket= ticketRepository.save(ticket);
        URI location = URI.create("/api/v1/tickets/" + savedTicket.getId());
        return ResponseEntity.created(location).body(savedTicket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketEntity> update(
            @PathVariable Long id,
            @RequestBody TicketEntity ticket
    ){
        log.info("Saving new ticket");
        Optional<TicketEntity> ticketOptional= ticketRepository.findById(id);
        if(ticketOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        TicketEntity existingTicket = ticketOptional.get();
        existingTicket.setTitle(ticket.getTitle());
        existingTicket.setDescription(ticket.getDescription());
        existingTicket.setRequesterName(ticket.getRequesterName());
        existingTicket.setRequesterEmail(ticket.getRequesterEmail());
        existingTicket.setPriority(ticket.getPriority());
        existingTicket.setStatus(ticket.getStatus());

        TicketEntity updatedTicket= ticketRepository.save(existingTicket);
        return ResponseEntity.ok(updatedTicket);
    }

}
