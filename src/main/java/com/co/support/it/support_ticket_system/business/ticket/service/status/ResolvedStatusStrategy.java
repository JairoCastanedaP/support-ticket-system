package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Resolver exige una nota de resolución, que queda guardada en el ticket junto con la fecha.
 */
@Component
public class ResolvedStatusStrategy implements TicketStatusChangeStrategy {

    @Override
    public TicketStatus targetStatus() {
        return TicketStatus.RESOLVED;
    }

    @Override
    public void apply(TicketEntity ticket, TicketStatusChangeRequestDto request) {
        if (!TicketStatusChangeStrategy.hasText(request.reason())) {
            throw new BadRequestException("La nota de resolución (reason) es obligatoria para pasar a RESOLVED");
        }

        ticket.setResolutionNote(request.reason().trim());
        ticket.setResolvedAt(LocalDateTime.now());
    }
}
