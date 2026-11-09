package com.co.support.it.support_ticket_system.integration.audit;

import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.entity.AuditOutboxEntity;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.repository.AuditOutboxRepository;
import com.co.support.it.support_ticket_system.infrastructure.persistence.mysql.outbox.service.AuditOutboxStatusService;
import com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.service.OracleAuditEventWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditOutboxRelayService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditOutboxRelayService.class);

    private final AuditOutboxRepository auditOutboxRepository;
    private final OracleAuditEventWriter oracleAuditEventWriter;
    private final AuditOutboxStatusService auditOutboxStatusService;

    public AuditOutboxRelayService(
            AuditOutboxRepository auditOutboxRepository,
            OracleAuditEventWriter oracleAuditEventWriter,
            AuditOutboxStatusService auditOutboxStatusService
    ) {
        this.auditOutboxRepository = auditOutboxRepository;
        this.oracleAuditEventWriter = oracleAuditEventWriter;
        this.auditOutboxStatusService = auditOutboxStatusService;
    }

    @Scheduled(fixedDelayString = "${app.audit-relay.fixed-delay-ms:5000}")
    public void relayPendingEvents() {
        List<AuditOutboxEntity> pendingEvents =
                auditOutboxRepository.findTop50ByDeliveryStatusOrderByOccurredAtUtcAsc(
                        AuditOutboxEntity.DeliveryStatus.PENDING
                );

        for (AuditOutboxEntity event : pendingEvents) {
            relay(event);
        }
    }

    private void relay(AuditOutboxEntity event) {
        try {
            oracleAuditEventWriter.storeIfAbsent(event);
        } catch (RuntimeException oracleError) {
            registerFailure(event, oracleError);
            return;
        }

        try {
            auditOutboxStatusService.markProcessed(event.getEventId());
        } catch (RuntimeException mysqlError) {
            logger.error(
                    "El evento se guardó en Oracle, pero no se pudo actualizar MySQL. eventId={}",
                    event.getEventId(),
                    mysqlError
            );
        }
    }

    private void registerFailure(
            AuditOutboxEntity event,
            RuntimeException oracleError
    ) {
        try {
            auditOutboxStatusService.registerDeliveryFailure(
                    event.getEventId(),
                    oracleError
            );
        } catch (RuntimeException mysqlError) {
            logger.error(
                    "Falló el envío a Oracle y no se pudo registrar el error en MySQL. eventId={}",
                    event.getEventId(),
                    mysqlError
            );
            return;
        }

        logger.warn(
                "No se pudo enviar el evento a Oracle. eventId={}",
                event.getEventId(),
                oracleError
        );
    }
}