package com.co.support.it.support_ticket_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequesterRequestDto(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no debe superar los 120 caracteres")
        String name,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 120, message = "El apellido  no debe superar los 120 caracteres")
        String surname,

        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 150, message = "El correo no debe superar los 150 caracteres")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @Size(max = 30, message = "El celular no debe superar los 30 caracteres")
        String phoneNumber
) {
}