package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;

public interface SupportAgentService
        extends GenericService<SupportAgentRequestDto, SupportAgentResponseDto, Long> {
}
