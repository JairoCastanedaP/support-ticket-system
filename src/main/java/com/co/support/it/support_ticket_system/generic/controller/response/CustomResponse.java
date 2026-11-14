package com.co.support.it.support_ticket_system.generic.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Respuesta estándar de todos los endpoints de la API.
 *
 * @param status    resultado de la operación
 * @param message   mensaje legible para el consumidor
 * @param timestamp fecha y hora de la respuesta
 * @param data      contenido de la respuesta; puede ser {@code null}
 */
public record CustomResponse<T>(
        ResponseStatus status,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime timestamp,
        T data
) {

    public CustomResponse(ResponseStatus status, String message, T data) {
        this(status, message, LocalDateTime.now(), data);
    }

    public static <T> CustomResponse<T> success(String message, T data) {
        return new CustomResponse<>(ResponseStatus.SUCCESS, message, data);
    }

    public static <T> CustomResponse<T> empty(String message) {
        return new CustomResponse<>(ResponseStatus.EMPTY, message, null);
    }

    public static <T> CustomResponse<T> error(String message, T data) {
        return new CustomResponse<>(ResponseStatus.ERROR, message, data);
    }
}
