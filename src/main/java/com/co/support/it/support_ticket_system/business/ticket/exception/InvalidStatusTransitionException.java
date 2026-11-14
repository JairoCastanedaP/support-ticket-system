package com.co.support.it.support_ticket_system.business.ticket.exception;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;

/**
 * El ticket no puede pasar del estado actual al estado solicitado.
 */
public class InvalidStatusTransitionException extends BusinessRuleException {

    public InvalidStatusTransitionException(TicketStatus current, TicketStatus target) {
        super("No se permite cambiar el ticket de " + current + " a " + target
                + ". Estados permitidos: " + (current.allowedTransitions().isEmpty()
                ? "ninguno, el ticket está cerrado"
                : current.allowedTransitions()));
    }
}
