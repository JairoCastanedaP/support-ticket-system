package com.co.support.it.support_ticket_system.business.ticket.domain;

/**
 * Estados del ciclo de vida de un ticket de soporte.
 * Las transiciones permitidas entre estados se definirán en la etapa de reglas de negocio.
 */
public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    WAITING_FOR_REQUESTER,
    RESOLVED,
    CLOSED
}
