package com.co.support.it.support_ticket_system.service;


import com.co.support.it.support_ticket_system.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.dto.RequesterResponseDto;

import java.util.List;

public interface RequesterService {

    List<RequesterResponseDto> findAll();

    RequesterResponseDto findById(Long id);

    RequesterResponseDto save(RequesterRequestDto requesterRequestDto);
}