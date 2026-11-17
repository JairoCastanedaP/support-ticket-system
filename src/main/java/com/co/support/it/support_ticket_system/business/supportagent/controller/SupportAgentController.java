package com.co.support.it.support_ticket_system.business.supportagent.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

@Tag(name = "Agentes de soporte", description = "Agentes que atienden los tickets. CRUD genérico con borrado lógico; el correo es único.")
public interface SupportAgentController
        extends GenericController<SupportAgentRequestDto, SupportAgentResponseDto, Long> {
}
