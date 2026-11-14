package com.co.support.it.support_ticket_system.business.requester.controller;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.controller.GenericController;

public interface RequesterController
        extends GenericController<RequesterRequestDto, RequesterResponseDto, Long> {
}
