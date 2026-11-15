package com.co.support.it.support_ticket_system.business.notification.service;

import com.co.support.it.support_ticket_system.business.notification.config.NotificationProperties;
import com.co.support.it.support_ticket_system.business.notification.domain.NotificationStatus;
import com.co.support.it.support_ticket_system.business.notification.entity.TicketNotificationEntity;
import com.co.support.it.support_ticket_system.business.notification.repository.TicketNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketNotificationProcessorTest {

    private TicketNotificationRepository repository;
    private JavaMailSender mailSender;
    private TicketNotificationEntity notification;

    @BeforeEach
    void setUp() {
        repository = mock(TicketNotificationRepository.class);
        mailSender = mock(JavaMailSender.class);

        notification = new TicketNotificationEntity();
        notification.setId(1L);
        notification.setSubject("Ticket TCK-2026-000001 resuelto: Sin red");
        notification.setBody("Cuerpo");
        when(repository.findById(1L)).thenReturn(Optional.of(notification));
    }

    private TicketNotificationProcessor processor(String adminEmail) {
        return new TicketNotificationProcessor(repository, mailSender,
                new NotificationProperties(true, adminEmail, "no-reply@support-ticket.local", 30, 3));
    }

    @Test
    void send_successMarksTheNotificationAsSent() {
        processor("admin@support-ticket.local").send(1L);

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(message.capture());
        assertThat(message.getValue().getTo()).containsExactly("admin@support-ticket.local");
        assertThat(message.getValue().getSubject()).contains("TCK-2026-000001");

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getAttempts()).isEqualTo(1);
        assertThat(notification.getSentAt()).isNotNull();
    }

    @Test
    void send_failureKeepsItPendingUntilTheLastAttempt() {
        doThrow(new MailSendException("SMTP caído")).when(mailSender).send(any(SimpleMailMessage.class));
        TicketNotificationProcessor processor = processor("admin@support-ticket.local");

        processor.send(1L);
        processor.send(1L);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getLastError()).contains("SMTP caído");

        processor.send(1L);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getAttempts()).isEqualTo(3);
    }

    @Test
    void send_withoutRecipientIsAFailedAttempt() {
        processor("").send(1L);

        verifyNoInteractions(mailSender);
        assertThat(notification.getAttempts()).isEqualTo(1);
        assertThat(notification.getLastError()).contains("TICKET_ADMIN_EMAIL");
    }

    @Test
    void send_ignoresNotificationsThatAreNoLongerPending() {
        notification.setStatus(NotificationStatus.SENT);

        processor("admin@support-ticket.local").send(1L);

        verifyNoInteractions(mailSender);
        verify(repository, never()).save(any());
    }
}
