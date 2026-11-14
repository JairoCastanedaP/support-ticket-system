package com.co.support.it.support_ticket_system.generic.exception;

/**
 * El recurso solicitado no existe. Se traduce a 404.
 */
public class NotFoundException extends GenericException {

    public NotFoundException(String message) {
        super(message);
    }
}
