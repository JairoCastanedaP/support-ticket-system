package com.co.support.it.support_ticket_system.business.ticket.exception;

import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;

public class TicketNotFoundException extends NotFoundException{

    public TicketNotFoundException(Long id){
        super("No se encontró el ticket con id: "+ id);
    }

    public TicketNotFoundException(String ticketNumber){
        super("No se encontró el ticket con número: " + ticketNumber);
    }
}
