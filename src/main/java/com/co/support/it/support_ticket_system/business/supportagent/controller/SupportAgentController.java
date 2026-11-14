package com.co.support.it.support_ticket_system.business.supportagent.controller;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

public interface SupportAgentController
        extends GenericController<SupportAgentRequestDto, SupportAgentResponseDto, Long> {
}
