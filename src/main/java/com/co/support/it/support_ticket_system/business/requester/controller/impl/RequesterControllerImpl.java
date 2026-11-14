package com.co.support.it.support_ticket_system.business.requester.controller.impl;

import com.co.support.it.support_ticket_system.business.requester.controller.RequesterController;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.service.RequesterService;
import com.co.support.it.support_ticket_system.generic.controller.GenericCrudControllerImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.co.support.it.support_ticket_system.business.shared.constants.APIEndPoints.REQUESTERS;

@RestController
@RequestMapping(REQUESTERS)
public class RequesterControllerImpl
        extends GenericCrudControllerImpl<RequesterRequestDto, RequesterResponseDto, Long>
        implements RequesterController {

    public RequesterControllerImpl(RequesterService requesterService) {
        super(requesterService, REQUESTERS);
    }
}
