package com.co.support.it.support_ticket_system.business.ticket.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.controller.response.PageResponse;
import com.co.support.it.support_ticket_system.integration.asset.AssetResponseDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.*;

/**
 * Consultas de solo lectura sobre tickets.
 */
@Tag(name = "Consultas de tickets", description = "Consultas de solo lectura: JPQL, SQL nativo con proyección, vista de base de datos, procedimiento almacenado y servicio externo.")
public interface TicketQueryController {

    @Operation(summary = "Buscar por número", description = "Consulta JPQL con join fetch (categoría, solicitante y agente en una sola consulta). No distingue mayúsculas.")
    @GetMapping(TICKET_BY_NUMBER)
    ResponseEntity<CustomResponse<TicketResponseDto>> findByTicketNumber(
            @PathVariable("ticketNumber") String ticketNumber
    );

    @Operation(summary = "Buscar por texto", description = "SQL nativo sobre título y descripción que devuelve una proyección con id, número, título, estado y prioridad (máximo 50). El texto debe tener entre 3 y 100 caracteres.")
    @GetMapping(TICKET_SEARCH)
    ResponseEntity<CustomResponse<List<TicketSearchResultDto>>> search(@RequestParam("text") String text);

    @Operation(summary = "Listado resumido", description = "Lee la vista vw_ticket_summary: nombres completos, cantidad de comentarios y horas abierto calculados por la base. Paginado y filtrable por estado y prioridad.")
    @GetMapping(TICKET_SUMMARY)
    ResponseEntity<CustomResponse<PageResponse<TicketSummaryResponseDto>>> findSummary(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @ParameterObject @PageableDefault(size = 20, sort = "ticketId", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "Indicadores", description = "Ejecuta el procedimiento usp_ticket_indicators: cantidad por estado y por categoría y horas promedio de resolución. 501 en el perfil local (H2).")
    @GetMapping(TICKET_INDICATORS)
    ResponseEntity<CustomResponse<TicketIndicatorsResponseDto>> getIndicators();

    /**
     * Detalle del activo de TI del ticket, consultado en el catálogo externo.
     */
    @Operation(summary = "Activo de TI del ticket", description = "Consulta el catálogo externo de activos (login y token Bearer). 404 si el ticket no tiene activo; 503 si el catálogo no responde.")
    @GetMapping(TICKET_ASSET)
    ResponseEntity<CustomResponse<AssetResponseDto>> findAsset(@PathVariable("id") Long id);
}
