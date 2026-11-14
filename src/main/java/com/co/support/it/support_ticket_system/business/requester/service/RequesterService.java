package com.co.support.it.support_ticket_system.business.requester.service;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;

public interface RequesterService
        extends GenericService<RequesterRequestDto, RequesterResponseDto, Long> {
}
