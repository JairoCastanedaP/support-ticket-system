package com.co.support.it.support_ticket_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketRequestDto (

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 150, message = "El titulo no debe superar los 150 caracteres")
        String title,

        @NotBlank(message = "La descripcion es obligatorio")
        @Size(max = 2000, message = "La descripcion no debe superar los 2000 caracteres")
        String description,

        @NotBlank(message = "El nombre del solicitante es obligatorio")
        @Size(max = 120, message = "El nombre no debe superar los 120 caracteres")
        String requesterName,

        @NotBlank(message = "El correo del solicitante es obligatorio")
        @Size(max = 150, message = "EL correo del solicitante no debe superar los 150 caracteres")
        @Email(message = "El correo no tiene un formato valido")
        String requesterEmail,

        @NotBlank(message = "La prioridad es obligatoria")
        @Size(max = 20, message = "La prioridad no debe superar los 20 caracteres")
        String priority ,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no debe tener más de 30 caracteres")
        String status
){
}
