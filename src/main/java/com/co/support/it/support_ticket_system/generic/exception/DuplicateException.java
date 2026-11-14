package com.co.support.it.support_ticket_system.generic.exception;

/**
 * El recurso ya existe con un valor que debe ser único. Se traduce a 409.
 */
public class DuplicateException extends GenericException {

    public DuplicateException(String message) {
        super(message);
    }
}
