package com.co.support.it.support_ticket_system.generic.exception;

/**
 * Raíz de las excepciones de negocio controladas por la aplicación.
 */
public abstract class GenericException extends RuntimeException {

    protected GenericException(String message) {
        super(message);
    }
}
