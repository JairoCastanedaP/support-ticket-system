package com.co.support.it.support_ticket_system.business.requester.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

@Tag(name = "Solicitantes", description = "Personas que reportan tickets. CRUD genérico con borrado lógico; el correo es único.")
public interface RequesterController
        extends GenericController<RequesterRequestDto, RequesterResponseDto, Long> {
}
