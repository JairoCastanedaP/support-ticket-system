package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

/**
 * El solicitante debe existir (404) y estar activo (409).
 */
@Component
public class RequesterValidationHandler extends TicketValidationHandler {

    private final RequesterRepository requesterRepository;

    public RequesterValidationHandler(RequesterRepository requesterRepository) {
        this.requesterRepository = requesterRepository;
    }

    @Override
    protected void check(TicketValidationContext context) {
        Long id = context.getRequest().requesterId();
        RequesterEntity requester = requesterRepository.findById(id)
                .orElseThrow(() -> new RequesterNotFoundException(id));

        if (!requester.isActive()) {
            throw new BusinessRuleException("El solicitante con id " + id + " está inactivo");
        }
        context.setRequester(requester);
    }
}
