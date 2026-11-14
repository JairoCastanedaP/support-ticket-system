package com.co.support.it.support_ticket_system.business.ticket.domain;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados del ciclo de vida de un ticket de soporte y las transiciones permitidas entre ellos.
 * CLOSED es un estado terminal.
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    WAITING_FOR_REQUESTER,
    RESOLVED,
    CLOSED;

    /**
     * Estados a los que se puede pasar desde el estado actual.
     */
    public Set<TicketStatus> allowedTransitions() {
        return switch (this) {
            case OPEN -> EnumSet.of(IN_PROGRESS, CLOSED);
            case IN_PROGRESS -> EnumSet.of(WAITING_FOR_REQUESTER, RESOLVED);
            case WAITING_FOR_REQUESTER -> EnumSet.of(IN_PROGRESS, CLOSED);
            case RESOLVED -> EnumSet.of(CLOSED, IN_PROGRESS);
            case CLOSED -> EnumSet.noneOf(TicketStatus.class);
        };
    }

    public boolean canTransitionTo(TicketStatus target) {
        return target != null && allowedTransitions().contains(target);
    }
}
