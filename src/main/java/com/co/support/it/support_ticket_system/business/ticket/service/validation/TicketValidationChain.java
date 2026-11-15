package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import org.springframework.stereotype.Component;

/**
 * Arma la cadena en orden: solicitante → categoría → agente → activo de TI.
 * Las validaciones locales van primero para no consultar el servicio externo si ya hay un error.
 */
@Component
public class TicketValidationChain {

    private final TicketValidationHandler first;

    public TicketValidationChain(
            RequesterValidationHandler requesterHandler,
            CategoryValidationHandler categoryHandler,
            SupportAgentValidationHandler supportAgentHandler,
            AssetValidationHandler assetHandler
    ) {
        requesterHandler.linkWith(categoryHandler).linkWith(supportAgentHandler).linkWith(assetHandler);
        this.first = requesterHandler;
    }

    public TicketValidationContext validate(TicketRequestDto request) {
        TicketValidationContext context = new TicketValidationContext(request);
        first.validate(context);
        return context;
    }
}
