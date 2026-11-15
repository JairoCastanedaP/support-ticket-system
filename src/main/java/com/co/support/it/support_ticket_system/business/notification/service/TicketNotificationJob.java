package com.co.support.it.support_ticket_system.business.notification.service;

import com.co.support.it.support_ticket_system.business.notification.domain.NotificationStatus;
import com.co.support.it.support_ticket_system.business.notification.entity.TicketNotificationEntity;
import com.co.support.it.support_ticket_system.business.notification.repository.TicketNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * Job de Quartz: toma los correos pendientes y los envía uno por uno.
 * No se ejecuta en paralelo consigo mismo para no enviar dos veces el mismo correo.
 */
@Slf4j
@DisallowConcurrentExecution
public class TicketNotificationJob extends QuartzJobBean {

    private final TicketNotificationRepository repository;
    private final TicketNotificationProcessor processor;

    public TicketNotificationJob(TicketNotificationRepository repository, TicketNotificationProcessor processor) {
        this.repository = repository;
        this.processor = processor;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        List<Long> pending = repository.findTop20ByStatusOrderByIdAsc(NotificationStatus.PENDING).stream()
                .map(TicketNotificationEntity::getId)
                .toList();

        if (!pending.isEmpty()) {
            log.info("Procesando {} correo(s) pendiente(s)", pending.size());
        }
        pending.forEach(processor::send);
    }
}
