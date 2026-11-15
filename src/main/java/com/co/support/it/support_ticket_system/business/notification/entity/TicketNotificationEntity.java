package com.co.support.it.support_ticket_system.business.notification.entity;

import com.co.support.it.support_ticket_system.business.notification.domain.NotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Correo pendiente de enviar. Se guarda en la misma transacción del cambio de estado
 * y lo envía después un job de Quartz, así un fallo de correo no revierte el cambio del ticket.
 */
@Entity
@Table(name = "ticket_notifications")
@Getter
@Setter
public class TicketNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "ticket_number", nullable = false, length = 40)
    private String ticketNumber;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 4000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(nullable = false)
    private int attempts;

    @Column(length = 150)
    private String recipient;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
