package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Cerrar un ticket resuelto no necesita motivo; cerrarlo sin haberlo resuelto sí.
 */
@Component
public class ClosedStatusStrategy implements TicketStatusChangeStrategy {

    @Override
    public TicketStatus targetStatus() {
        return TicketStatus.CLOSED;
    }

    @Override
    public void apply(TicketEntity ticket, TicketStatusChangeRequestDto request) {
        if (ticket.getStatus() != TicketStatus.RESOLVED
                && !TicketStatusChangeStrategy.hasText(request.reason())) {
            throw new BadRequestException(
                    "El motivo (reason) es obligatorio para cerrar un ticket que no fue resuelto");
        }

        ticket.setClosedAt(LocalDateTime.now());
    }
}
