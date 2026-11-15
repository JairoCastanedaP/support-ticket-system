package com.co.support.it.support_ticket_system.business.ticket.projection;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;

/**
 * Projection: solo las columnas que necesita el resultado de una búsqueda.
 * Spring Data la implementa a partir de los alias de la consulta nativa.
 */
public interface TicketSearchProjection {

    Long getId();

    String getTicketNumber();

    String getTitle();

    TicketStatus getStatus();

    TicketPriority getPriority();
}
