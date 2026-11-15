package com.co.support.it.support_ticket_system.generic.exception;

/**
 * Un servicio externo no respondió o respondió con error. Se traduce a 503.
 */
public class ExternalServiceException extends GenericException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
