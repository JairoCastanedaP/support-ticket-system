package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

/**
 * El agente es opcional; si viene, debe existir (404) y estar activo (409).
 */
@Component
public class SupportAgentValidationHandler extends TicketValidationHandler {

    private final SupportAgentRepository supportAgentRepository;

    public SupportAgentValidationHandler(SupportAgentRepository supportAgentRepository) {
        this.supportAgentRepository = supportAgentRepository;
    }

    @Override
    protected void check(TicketValidationContext context) {
        Long id = context.getRequest().supportAgentId();
        if (id == null) {
            return;
        }

        SupportAgentEntity agent = supportAgentRepository.findById(id)
                .orElseThrow(() -> new SupportAgentNotFoundException(id));

        if (!agent.isActive()) {
            throw new BusinessRuleException("El agente de soporte con id " + id + " está inactivo");
        }
        context.setSupportAgent(agent);
    }
}
