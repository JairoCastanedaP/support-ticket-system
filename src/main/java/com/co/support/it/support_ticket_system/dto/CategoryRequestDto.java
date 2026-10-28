package com.co.support.it.support_ticket_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(

        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 80, message = "El nombre no debe superar los 80 caracteres")
        String name,

        @Size(max = 250, message = "La descripción no debe superar los 250 caracteres")
        String description
) {
}