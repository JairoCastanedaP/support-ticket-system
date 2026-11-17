package com.co.support.it.support_ticket_system.business.ticket.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusHistoryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.BY_ID;
import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKET_STATUS;
import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.TICKET_STATUS_HISTORY;

/**
 * Contrato REST de tickets. Tiene operaciones propias (filtros, paginación, historial),
 * por eso no extiende el CRUD genérico.
 */
@Tag(name = "Tickets", description = "Ciclo de vida del ticket: creación, edición, cambio de estado, borrado lógico e historial.")
public interface TicketController {

    @Operation(summary = "Listar tickets", description = "Listado paginado (por defecto 20, del más reciente al más antiguo) con filtros opcionales por estado, prioridad, categoría y agente.")
    @GetMapping
    ResponseEntity<CustomResponse<PageResponse<TicketResponseDto>>> findAll(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supportAgentId,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "Consultar ticket", description = "404 si no existe o fue eliminado.")
    @GetMapping(BY_ID)
    ResponseEntity<CustomResponse<TicketResponseDto>> findById(@PathVariable("id") Long id);

    @Operation(summary = "Crear ticket", description = "El ticket nace en OPEN con número TCK-AAAA-NNNNNN. Una cadena de validación revisa solicitante, categoría, agente y activo de TI: 404 si no existen, 409 si están inactivos, 503 si el catálogo de activos no responde.")
    @PostMapping
    ResponseEntity<CustomResponse<TicketResponseDto>> save(@Valid @RequestBody TicketRequestDto request);

    @Operation(summary = "Editar ticket", description = "Actualiza los datos editables; el estado no cambia aquí. 409 si el ticket está cerrado.")
    @PutMapping(BY_ID)
    ResponseEntity<CustomResponse<TicketResponseDto>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody TicketRequestDto request
    );

    /**
     * Cambia el estado del ticket según las transiciones permitidas. Es la única vía para cambiarlo.
     */
    @Operation(summary = "Cambiar estado", description = "Única vía para cambiar el estado. Transiciones: OPEN→IN_PROGRESS/CLOSED; IN_PROGRESS→WAITING_FOR_REQUESTER/RESOLVED; WAITING_FOR_REQUESTER→IN_PROGRESS/CLOSED; RESOLVED→CLOSED/IN_PROGRESS; CLOSED es terminal. 409 si la transición no está permitida o si pasa a IN_PROGRESS sin agente; 400 si falta la nota al resolver o el motivo al cerrar sin resolver. Al resolver se programa el correo al administrador.")
    @PatchMapping(TICKET_STATUS)
    ResponseEntity<CustomResponse<TicketResponseDto>> changeStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody TicketStatusChangeRequestDto request
    );

    @Operation(summary = "Eliminar ticket (borrado lógico)", description = "Conserva el ticket y su historial en la base; deja de aparecer en las consultas.")
    @DeleteMapping(BY_ID)
    ResponseEntity<CustomResponse<Void>> delete(@PathVariable("id") Long id);

    @Operation(summary = "Historial de estados", description = "Cambios de estado en orden, con motivo y usuario (X-User-Id).")
    @GetMapping(TICKET_STATUS_HISTORY)
    ResponseEntity<CustomResponse<List<TicketStatusHistoryResponseDto>>> findStatusHistory(
            @PathVariable("id") Long id
    );
}
