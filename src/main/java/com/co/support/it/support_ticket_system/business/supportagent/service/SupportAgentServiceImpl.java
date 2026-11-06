package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.mapper.SupportAgentMapper;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;
import com.co.support.it.support_ticket_system.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SupportAgentServiceImpl
        extends GenericServiceImpl<SupportAgentEntity, SupportAgentRequestDto, SupportAgentResponseDto, Long>
        implements SupportAgentService {

    public SupportAgentServiceImpl(
            SupportAgentRepository supportAgentRepository,
            SupportAgentMapper supportAgentMapper
    ) {
        super(
                supportAgentRepository,
                supportAgentMapper::toEntity,
                supportAgentMapper::toResponseDto
        );
    }

    @Override
    protected RuntimeException createNotFoundException(Long id) {
        return new SupportAgentNotFoundException(id);
    }
}

/*
package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.mapper.SupportAgentMapper;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;
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
}*/