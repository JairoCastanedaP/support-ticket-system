package com.co.support.it.support_ticket_system.generic.exception;

/**
 * La operación viola una regla de negocio. Se traduce a 409.
 */
public class BusinessRuleException extends GenericException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
