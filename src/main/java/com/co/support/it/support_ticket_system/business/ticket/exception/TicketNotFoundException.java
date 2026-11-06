package com.co.support.it.support_ticket_system.business.ticket.exception;

public class TicketNotFoundException extends RuntimeException{

    public TicketNotFoundException(Long id){
        super("No se encontró el ticket con id: "+ id);
    }
}
