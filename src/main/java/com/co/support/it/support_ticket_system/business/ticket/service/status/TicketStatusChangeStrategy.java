package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;

/**
 * Strategy: reglas y efectos de llegar a un estado concreto.
 * Cada implementación atiende un estado destino; el servicio elige la que corresponde.
 * Se ejecuta antes de cambiar el estado, así que {@code ticket.getStatus()} todavía es el estado anterior.
 */
public interface TicketStatusChangeStrategy {

    TicketStatus targetStatus();

    void apply(TicketEntity ticket, TicketStatusChangeRequestDto request);

    static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
