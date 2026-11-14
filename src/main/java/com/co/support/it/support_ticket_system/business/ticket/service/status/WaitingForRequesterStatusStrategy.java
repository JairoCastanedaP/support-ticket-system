package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import org.springframework.stereotype.Component;

/**
 * Esperar respuesta del solicitante no tiene reglas adicionales; el motivo es opcional.
 */
@Component
public class WaitingForRequesterStatusStrategy implements TicketStatusChangeStrategy {

    @Override
    public TicketStatus targetStatus() {
        return TicketStatus.WAITING_FOR_REQUESTER;
    }

    @Override
    public void apply(TicketEntity ticket, TicketStatusChangeRequestDto request) {
        // Sin reglas adicionales.
    }
}
