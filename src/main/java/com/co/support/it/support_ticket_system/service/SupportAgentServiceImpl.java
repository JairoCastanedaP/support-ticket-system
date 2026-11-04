package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.mapper.SupportAgentMapper;
import com.co.support.it.support_ticket_system.repository.SupportAgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SupportAgentServiceImpl implements SupportAgentService {

    private final SupportAgentRepository supportAgentRepository;
    private final SupportAgentMapper supportAgentMapper;

    public SupportAgentServiceImpl(
            SupportAgentRepository supportAgentRepository,
            SupportAgentMapper supportAgentMapper
    ) {
        this.supportAgentRepository = supportAgentRepository;
        this.supportAgentMapper = supportAgentMapper;
    }

    @Override
    public List<SupportAgentResponseDto> findAll() {
        return supportAgentMapper.toResponseDtoList(
                supportAgentRepository.findAll()
        );
    }

    @Override
    public SupportAgentResponseDto findById(Long id) {
        SupportAgentEntity supportAgent = supportAgentRepository.findById(id)
                .orElseThrow(() -> new SupportAgentNotFoundException(id));

        return supportAgentMapper.toResponseDto(supportAgent);
    }

    @Override
    @Transactional
    public SupportAgentResponseDto save(SupportAgentRequestDto requestDto) {
        SupportAgentEntity supportAgent = supportAgentMapper.toEntity(requestDto);
        SupportAgentEntity savedSupportAgent = supportAgentRepository.save(supportAgent);

        return supportAgentMapper.toResponseDto(savedSupportAgent);
    }
}