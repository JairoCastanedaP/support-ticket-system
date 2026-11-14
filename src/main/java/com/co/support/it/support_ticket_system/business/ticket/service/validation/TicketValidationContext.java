package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Datos que recorren la cadena de validación. Cada eslabón deja aquí la entidad que validó
 * para que el servicio la use sin volver a consultarla.
 */
@Getter
@Setter
public class TicketValidationContext {

    private final TicketRequestDto request;
    private RequesterEntity requester;
    private CategoryEntity category;
    private SupportAgentEntity supportAgent;

    public TicketValidationContext(TicketRequestDto request) {
        this.request = request;
    }
}
