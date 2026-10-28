package com.co.support.it.support_ticket_system.exception;

public class RequesterNotFoundException extends RuntimeException {

    public RequesterNotFoundException(Long id) {
        super("No existe el solicitante con id: " + id);
    }
}