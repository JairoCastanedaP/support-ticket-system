package com.co.support.it.support_ticket_system.business.batch.controller.impl;

import com.co.support.it.support_ticket_system.business.batch.controller.AuditHistoryBatchController;
import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistoryRunResponseDto;
import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistorySummaryResponseDto;
import com.co.support.it.support_ticket_system.business.batch.service.AuditHistoryBatchService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.BATCH_AUDIT_HISTORY;

@RestController
@RequestMapping(BATCH_AUDIT_HISTORY)
public class AuditHistoryBatchControllerImpl extends GenericControllerImpl implements AuditHistoryBatchController {

    private final AuditHistoryBatchService batchService;

    public AuditHistoryBatchControllerImpl(AuditHistoryBatchService batchService) {
        this.batchService = batchService;
    }

    @Override
    public ResponseEntity<CustomResponse<AuditHistoryRunResponseDto>> run() {
        return returnOk(batchService.run());
    }

    @Override
    public ResponseEntity<CustomResponse<AuditHistorySummaryResponseDto>> summary() {
        return returnOk(batchService.summary());
    }
}
