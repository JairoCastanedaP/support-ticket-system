package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

/**
 * Para atender un ticket debe haber un agente asignado.
 * Si el ticket estaba resuelto, se reabre y se descarta la resolución anterior.
 */
@Component
public class InProgressStatusStrategy implements TicketStatusChangeStrategy {

    @Override
    public TicketStatus targetStatus() {
        return TicketStatus.IN_PROGRESS;
    }

    @Override
    public void apply(TicketEntity ticket, TicketStatusChangeRequestDto request) {
        if (ticket.getAssignedAgent() == null) {
            throw new BusinessRuleException(
                    "El ticket debe tener un agente asignado para pasar a IN_PROGRESS");
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            ticket.setResolutionNote(null);
            ticket.setResolvedAt(null);
        }
    }
}
