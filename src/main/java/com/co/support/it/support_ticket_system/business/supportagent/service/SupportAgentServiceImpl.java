package com.co.support.it.support_ticket_system.business.supportagent.service;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.exception.SupportAgentNotFoundException;
import com.co.support.it.support_ticket_system.business.supportagent.mapper.SupportAgentMapper;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;
import com.co.support.it.support_ticket_system.generic.exception.DuplicateException;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SupportAgentServiceImpl
        extends GenericServiceImpl<SupportAgentEntity, SupportAgentRequestDto, SupportAgentResponseDto, Long>
        implements SupportAgentService {

    private final SupportAgentRepository supportAgentRepository;

    public SupportAgentServiceImpl(
            SupportAgentRepository supportAgentRepository,
            SupportAgentMapper supportAgentMapper
    ) {
        super(
                supportAgentRepository,
                supportAgentMapper::toEntity,
                supportAgentMapper::toResponseDto,
                supportAgentMapper::updateEntity
        );
        this.supportAgentRepository = supportAgentRepository;
    }

    @Override
    protected NotFoundException createNotFoundException(Long id) {
        return new SupportAgentNotFoundException(id);
    }

    @Override
    protected void validateUniqueness(SupportAgentRequestDto request, Long currentId) {
        boolean duplicated = currentId == null
                ? supportAgentRepository.existsByEmailIgnoreCase(request.email())
                : supportAgentRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), currentId);

        if (duplicated) {
            throw new DuplicateException(
                    "Ya existe un registro de agente de soporte con el correo: " + request.email()
            );
        }
    }
}
