package com.co.support.it.support_ticket_system.business.requester.controller;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.REQUESTER_FROM_DIRECTORY;

/**
 * Alta de solicitantes desde el directorio interno de empleados.
 */
public interface RequesterDirectoryController {

    @PostMapping(REQUESTER_FROM_DIRECTORY)
    ResponseEntity<CustomResponse<RequesterResponseDto>> createFromDirectory(@PathVariable("employeeId") Long employeeId);
}
