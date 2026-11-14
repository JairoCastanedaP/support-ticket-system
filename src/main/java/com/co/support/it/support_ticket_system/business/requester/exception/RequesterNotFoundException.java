package com.co.support.it.support_ticket_system.business.requester.exception;

import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;

public class RequesterNotFoundException extends NotFoundException {

    public RequesterNotFoundException(Long id) {
        super("No existe el solicitante con id: " + id);
    }
}