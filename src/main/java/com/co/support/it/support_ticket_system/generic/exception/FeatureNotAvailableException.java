package com.co.support.it.support_ticket_system.generic.exception;

/**
 * La funcionalidad existe pero no está disponible con la configuración actual. Se traduce a 501.
 */
public class FeatureNotAvailableException extends GenericException {

    public FeatureNotAvailableException(String message) {
        super(message);
    }
}
