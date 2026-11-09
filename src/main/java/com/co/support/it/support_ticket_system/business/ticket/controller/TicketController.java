package com.co.support.it.support_ticket_system.business.ticket.controller;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService= ticketService;
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponseDto>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supportAgentId,
            @PageableDefault(
                    size = 20,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ticketService.findAll(
                        status,
                        priority,
                        categoryId,
                        supportAgentId,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TicketResponseDto> save(@Valid @RequestBody TicketRequestDto ticket){
        //log.info("Saving Ticket: {}",ticket);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
