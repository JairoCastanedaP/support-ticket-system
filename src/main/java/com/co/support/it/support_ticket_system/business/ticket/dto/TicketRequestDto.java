package com.co.support.it.support_ticket_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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

        @NotBlank(message = "La prioridad es obligatoria")
        @Size(max = 20, message = "La prioridad no debe superar los 20 caracteres")
        String priority ,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no debe tener más de 30 caracteres")
        String status
){
}
