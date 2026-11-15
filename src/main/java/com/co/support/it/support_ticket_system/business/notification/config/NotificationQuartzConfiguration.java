package com.co.support.it.support_ticket_system.business.notification.config;

import com.co.support.it.support_ticket_system.business.notification.service.TicketNotificationJob;
import org.quartz.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programa el job de envío de correos. Solo se activa con app.notifications.enabled=true (perfil docker);
 * Quartz usa su almacenamiento en memoria porque el estado de cada correo vive en ticket_notifications.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.notifications", name = "enabled", havingValue = "true")
public class NotificationQuartzConfiguration {

    @Bean
    public JobDetail ticketNotificationJobDetail() {
        return JobBuilder.newJob(TicketNotificationJob.class)
                .withIdentity("ticketNotificationJob", "notifications")
                .withDescription("Envía al administrador los correos pendientes de tickets resueltos")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ticketNotificationTrigger(JobDetail ticketNotificationJobDetail, NotificationProperties properties) {
        return TriggerBuilder.newTrigger()
                .forJob(ticketNotificationJobDetail)
                .withIdentity("ticketNotificationTrigger", "notifications")
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(properties.intervalSeconds()))
                .build();
    }
}
