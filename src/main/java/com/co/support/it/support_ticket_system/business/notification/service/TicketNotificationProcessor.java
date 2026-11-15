package com.co.support.it.support_ticket_system.business.notification.service;

import com.co.support.it.support_ticket_system.business.notification.config.NotificationProperties;
import com.co.support.it.support_ticket_system.business.notification.domain.NotificationStatus;
import com.co.support.it.support_ticket_system.business.notification.entity.TicketNotificationEntity;
import com.co.support.it.support_ticket_system.business.notification.repository.TicketNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Envía un correo pendiente en su propia transacción: el resultado de uno no afecta a los demás.
 * Si falla, suma un intento; al llegar al máximo queda en FAILED.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.notifications", name = "enabled", havingValue = "true")
public class TicketNotificationProcessor {

    private final TicketNotificationRepository repository;
    private final JavaMailSender mailSender;
    private final NotificationProperties properties;

    public TicketNotificationProcessor(
            TicketNotificationRepository repository,
            JavaMailSender mailSender,
            NotificationProperties properties
    ) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long notificationId) {
        TicketNotificationEntity notification = repository.findById(notificationId).orElse(null);
        if (notification == null || notification.getStatus() != NotificationStatus.PENDING) {
            return;
        }

        String recipient = properties.adminEmail();
        notification.setAttempts(notification.getAttempts() + 1);

        try {
            if (recipient == null || recipient.isBlank()) {
                throw new IllegalStateException("TICKET_ADMIN_EMAIL no está configurado");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(properties.from());
            message.setTo(recipient);
            message.setSubject(notification.getSubject());
            message.setText(notification.getBody());
            mailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setRecipient(recipient);
            notification.setSentAt(LocalDateTime.now());
            notification.setLastError(null);
            log.info("Correo enviado: {} -> {}", notification.getSubject(), recipient);
        } catch (MailException | IllegalStateException exception) {
            notification.setLastError(truncate(exception.getMessage()));
            if (notification.getAttempts() >= properties.maxAttempts()) {
                notification.setStatus(NotificationStatus.FAILED);
            }
            log.warn("No se pudo enviar el correo {} (intento {}): {}",
                    notification.getId(), notification.getAttempts(), exception.getMessage());
        }

        repository.save(notification);
    }

    private static String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }
}
