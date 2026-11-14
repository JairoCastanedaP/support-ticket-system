package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cambio de estado de un ticket. El motivo es la nota de resolución al pasar a RESOLVED
 * y la justificación al cerrar un ticket que no fue resuelto.
 */
public record TicketStatusChangeRequestDto(

        @NotNull(message = "El nuevo estado es obligatorio")
        TicketStatus status,

        @Size(max = 1000, message = "El motivo no debe superar los 1000 caracteres")
        String reason
) {
}
