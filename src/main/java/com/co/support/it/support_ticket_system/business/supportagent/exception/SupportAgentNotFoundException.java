package com.co.support.it.support_ticket_system.business.supportagent.exception;

import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;

public class SupportAgentNotFoundException extends NotFoundException {

    public SupportAgentNotFoundException(Long id) {
        super("No se encontró el agente de soporte con id: " + id);
    }
}