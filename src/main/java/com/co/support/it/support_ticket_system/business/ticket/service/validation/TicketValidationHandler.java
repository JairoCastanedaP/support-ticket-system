package com.co.support.it.support_ticket_system.business.ticket.service.validation;

/**
 * Chain of Responsibility: cada eslabón valida una parte del ticket y, si todo está bien,
 * delega en el siguiente. El primero que falla corta la cadena con una excepción.
 */
public abstract class TicketValidationHandler {

    private TicketValidationHandler next;

    /**
     * Enlaza el siguiente eslabón y lo retorna para encadenar llamadas.
     */
    public TicketValidationHandler linkWith(TicketValidationHandler next) {
        this.next = next;
        return next;
    }

    public void validate(TicketValidationContext context) {
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void check(TicketValidationContext context);
}
