package com.co.support.it.support_ticket_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketCommentRequestDto(
        @NotBlank(message = "El comentario es obligatorio")
        @Size(max = 2000, message = "El comentario no debe superar los 2000 caracteres")
        String comment
) {
}