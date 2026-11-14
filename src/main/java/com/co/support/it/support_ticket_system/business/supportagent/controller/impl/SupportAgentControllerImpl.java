package com.co.support.it.support_ticket_system.business.supportagent.controller.impl;

import com.co.support.it.support_ticket_system.business.supportagent.controller.SupportAgentController;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.service.SupportAgentService;
import com.co.support.it.support_ticket_system.generic.controller.GenericCrudControllerImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.SUPPORT_AGENTS;

@RestController
@RequestMapping(SUPPORT_AGENTS)
public class SupportAgentControllerImpl
        extends GenericCrudControllerImpl<SupportAgentRequestDto, SupportAgentResponseDto, Long>
        implements SupportAgentController {

    public SupportAgentControllerImpl(SupportAgentService supportAgentService) {
        super(supportAgentService, SUPPORT_AGENTS);
    }
}
