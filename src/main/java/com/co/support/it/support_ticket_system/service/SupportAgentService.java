package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.dto.SupportAgentResponseDto;

import java.util.List;

public interface SupportAgentService {

    List<SupportAgentResponseDto> findAll();

    SupportAgentResponseDto findById(Long id);

    SupportAgentResponseDto save(SupportAgentRequestDto requestDto);
}