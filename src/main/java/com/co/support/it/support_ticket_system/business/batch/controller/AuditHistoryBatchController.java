package com.co.support.it.support_ticket_system.business.batch.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistoryRunResponseDto;
import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistorySummaryResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.SUMMARY;

/**
 * Histórico de auditoría: lanzamiento del volcado (síncrono) y resumen de lo copiado.
 */
@Tag(name = "Histórico de auditoría", description = "Spring Batch: copia la auditoría central (Oracle) al histórico (PostgreSQL).")
public interface AuditHistoryBatchController {

    @Operation(summary = "Lanzar volcado del histórico", description = "Síncrono. Copia los eventos de auditoría posteriores al último ya copiado (con una ventana de 10 minutos), en bloques del tamaño configurado en batch_job_config. Responde con leídos, ya copiados, escritos, bloques y duración. 409 si ya hay un volcado en curso.")
    @PostMapping
    ResponseEntity<CustomResponse<AuditHistoryRunResponseDto>> run();

    @Operation(summary = "Resumen del histórico", description = "Total de eventos copiados, por categoría y tipo, y fecha del último evento copiado.")
    @GetMapping(SUMMARY)
    ResponseEntity<CustomResponse<AuditHistorySummaryResponseDto>> summary();
}
