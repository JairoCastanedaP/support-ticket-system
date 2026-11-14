package com.co.support.it.support_ticket_system.business.ticket.controller;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.BY_ID;
import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKET_STATUS_HISTORY;

/**
 * Contrato REST de tickets. Tiene operaciones propias (filtros, paginación, historial),
 * por eso no extiende el CRUD genérico.
 */
public interface TicketController {

    @GetMapping
    ResponseEntity<CustomResponse<PageResponse<TicketResponseDto>>> findAll(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supportAgentId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    );

    @GetMapping(BY_ID)
    ResponseEntity<CustomResponse<TicketResponseDto>> findById(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<CustomResponse<TicketResponseDto>> save(@Valid @RequestBody TicketRequestDto request);

    @PutMapping(BY_ID)
    ResponseEntity<CustomResponse<TicketResponseDto>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody TicketRequestDto request
    );

    @DeleteMapping(BY_ID)
    ResponseEntity<CustomResponse<Void>> delete(@PathVariable("id") Long id);

    @GetMapping(TICKET_STATUS_HISTORY)
    ResponseEntity<CustomResponse<List<TicketStatusHistoryResponseDto>>> findStatusHistory(
            @PathVariable("id") Long id
    );
}
