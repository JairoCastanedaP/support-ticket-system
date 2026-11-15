package com.co.support.it.support_ticket_system.business.notification.service;

import com.co.support.it.support_ticket_system.business.notification.entity.TicketNotificationEntity;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.notification.repository.TicketNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registra los correos pendientes. No envía nada: el envío lo hace el job de Quartz.
 */
@Service
public class TicketNotificationService {

    public static final String TICKET_RESOLVED = "TICKET_RESOLVED";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TicketNotificationRepository repository;

    public TicketNotificationService(TicketNotificationRepository repository) {
        this.repository = repository;
    }

    /**
     * Debe llamarse dentro de la transacción del cambio de estado.
     */
    @Transactional
    public void registerTicketResolved(TicketEntity ticket, String resolvedBy) {
        TicketNotificationEntity notification = new TicketNotificationEntity();
        notification.setTicketId(ticket.getId());
        notification.setTicketNumber(ticket.getTicketNumber());
        notification.setEventType(TICKET_RESOLVED);
        notification.setSubject("Ticket " + ticket.getTicketNumber() + " resuelto: " + ticket.getTitle());
        notification.setBody(buildResolvedBody(ticket, resolvedBy));
        notification.setCreatedAt(LocalDateTime.now());

        repository.save(notification);
    }

    private String buildResolvedBody(TicketEntity ticket, String resolvedBy) {
        String agent = ticket.getAssignedAgent() == null
                ? "Sin asignar"
                : ticket.getAssignedAgent().getName() + " " + ticket.getAssignedAgent().getSurname();
        String resolvedAt = ticket.getResolvedAt() == null ? "-" : ticket.getResolvedAt().format(DATE_FORMAT);

        return """
                Hola,

                El ticket %s fue resuelto.

                Título: %s
                Categoría: %s
                Prioridad: %s
                Solicitante: %s %s (%s)
                Agente: %s
                Nota de resolución: %s
                Resuelto por: %s
                Fecha de resolución: %s

                Este es un mensaje automático del Support Ticket System.
                """.formatted(
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getCategory().getName(),
                ticket.getPriority(),
                ticket.getRequester().getName(), ticket.getRequester().getSurname(), ticket.getRequester().getEmail(),
                agent,
                ticket.getResolutionNote(),
                resolvedBy,
                resolvedAt
        );
    }
}
