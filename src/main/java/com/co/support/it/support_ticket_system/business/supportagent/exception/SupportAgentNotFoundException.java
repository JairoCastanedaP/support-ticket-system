package com.co.support.it.support_ticket_system.business.supportagent.exception;

public class SupportAgentNotFoundException extends RuntimeException {

    public SupportAgentNotFoundException(Long id) {
        super("No se encontró el agente de soporte con id: " + id);
    }
}