package com.co.support.it.support_ticket_system.generic.exception;

/**
 * La solicitud no trae un dato que la operación necesita según su contexto. Se traduce a 400.
 */
public class BadRequestException extends GenericException {

    public BadRequestException(String message) {
        super(message);
    }
}
