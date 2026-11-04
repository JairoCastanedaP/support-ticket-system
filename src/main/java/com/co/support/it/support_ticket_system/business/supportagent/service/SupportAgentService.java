/*package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;

import java.util.List;

public interface SupportAgentService {

    List<SupportAgentResponseDto> findAll();

    SupportAgentResponseDto findById(Long id);

    SupportAgentResponseDto save(SupportAgentRequestDto requestDto);
}*/

package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;

public interface SupportAgentService
        extends GenericService<SupportAgentRequestDto, SupportAgentResponseDto, Long> {
}