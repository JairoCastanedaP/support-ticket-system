package com.co.support.it.support_ticket_system.exception;

public class SupportAgentNotFoundException extends RuntimeException {

    public SupportAgentNotFoundException(Long id) {
        super("No se encontró el agente de soporte con id: " + id);
    }
}