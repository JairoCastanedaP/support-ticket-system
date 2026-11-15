package com.co.support.it.support_ticket_system.business.requester.controller.impl;

import com.co.support.it.support_ticket_system.business.requester.controller.RequesterDirectoryController;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.service.RequesterDirectoryService;
import com.co.support.it.support_ticket_system.generic.controller.GenericControllerImpl;
import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.REQUESTERS;

@RestController
@RequestMapping(REQUESTERS)
public class RequesterDirectoryControllerImpl extends GenericControllerImpl implements RequesterDirectoryController {

    private final RequesterDirectoryService requesterDirectoryService;

    public RequesterDirectoryControllerImpl(RequesterDirectoryService requesterDirectoryService) {
        this.requesterDirectoryService = requesterDirectoryService;
    }

    @Override
    public ResponseEntity<CustomResponse<RequesterResponseDto>> createFromDirectory(Long employeeId) {
        RequesterResponseDto created = requesterDirectoryService.createFromDirectory(employeeId);
        return returnCreated(REQUESTERS + "/" + created.id(), created);
    }
}
