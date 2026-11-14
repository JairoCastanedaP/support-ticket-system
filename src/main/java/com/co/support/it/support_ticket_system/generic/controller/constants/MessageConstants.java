package com.co.support.it.support_ticket_system.generic.controller.constants;

/**
 * Mensajes estándar de las respuestas de la API.
 */
public final class MessageConstants {

    public static final String SUCCESS_MESSAGE = "Operación realizada con éxito";
    public static final String CREATED_MESSAGE = "Registro creado con éxito";
    public static final String UPDATED_MESSAGE = "Registro actualizado con éxito";
    public static final String DELETED_MESSAGE = "Registro eliminado con éxito";
    public static final String EMPTY_MESSAGE = "No se encontraron registros";
    public static final String VALIDATION_MESSAGE = "Error de validación en los campos enviados";
    public static final String INVALID_BODY_MESSAGE = "El cuerpo de la solicitud no tiene un formato JSON válido";
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "Método HTTP no soportado para este recurso";
    public static final String INTERNAL_ERROR_MESSAGE = "Ocurrió un error inesperado. Intente nuevamente más tarde";

    private MessageConstants() {
    }
}
