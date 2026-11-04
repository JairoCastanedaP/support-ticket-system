/*
package com.co.support.it.support_ticket_system.business.requester.service;


import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;

import java.util.List;

public interface RequesterService {

    List<RequesterResponseDto> findAll();

    RequesterResponseDto findById(Long id);

    RequesterResponseDto save(RequesterRequestDto requesterRequestDto);
}
*/
package com.co.support.it.support_ticket_system.business.requester.service;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;

public interface RequesterService
        extends GenericService<RequesterRequestDto, RequesterResponseDto, Long> {
}