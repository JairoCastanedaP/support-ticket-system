package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Datos editables de un ticket. El estado no se recibe aquí: el ticket nace en OPEN
 * y cambia de estado únicamente con PATCH /tickets/{id}/status.
 */
public record TicketRequestDto (

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 150, message = "El titulo no debe superar los 150 caracteres")
        String title,

        @NotBlank(message = "La descripcion es obligatorio")
        @Size(max = 2000, message = "La descripcion no debe superar los 2000 caracteres")
        String description,

        @NotNull(message = "El solicitante es obligatorio")
        @Positive(message = "El identificador del solicitante debe ser positivo")
        Long requesterId,

        @NotNull(message = "La categoría es obligatoria")
        @Positive(message = "El identificador de la categoría debe ser positivo")
        Long categoryId,

        @Positive(message = "El identificador del agente debe ser positivo")
        Long supportAgentId,

        @NotNull(message = "La prioridad es obligatoria")
        TicketPriority priority
){
}
