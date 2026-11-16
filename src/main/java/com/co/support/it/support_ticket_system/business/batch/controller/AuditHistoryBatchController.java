package com.co.support.it.support_ticket_system.business.batch.controller;

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
public interface AuditHistoryBatchController {

    @PostMapping
    ResponseEntity<CustomResponse<AuditHistoryRunResponseDto>> run();

    @GetMapping(SUMMARY)
    ResponseEntity<CustomResponse<AuditHistorySummaryResponseDto>> summary();
}
