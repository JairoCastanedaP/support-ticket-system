
package com.co.support.it.support_ticket_system.business.requester.service;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.mapper.RequesterMapper;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RequesterServiceImpl
        extends GenericServiceImpl<RequesterEntity, RequesterRequestDto, RequesterResponseDto, Long>
        implements RequesterService {

    public RequesterServiceImpl(
            RequesterRepository requesterRepository,
            RequesterMapper requesterMapper
    ) {
        super(
                requesterRepository,
                requesterMapper::toEntity,
                requesterMapper::toResponseDto
        );
    }

    @Override
    protected RuntimeException createNotFoundException(Long id) {
        return new RequesterNotFoundException(id);
    }
}
/*
package com.co.support.it.support_ticket_system.business.requester.service;


import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.mapper.RequesterMapper;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequesterServiceImpl implements RequesterService {

    private final RequesterRepository requesterRepository;
    private final RequesterMapper requesterMapper;

    public RequesterServiceImpl(
            RequesterRepository requesterRepository,
            RequesterMapper requesterMapper
    ) {
        this.requesterRepository = requesterRepository;
        this.requesterMapper = requesterMapper;
    }

    @Override
    public List<RequesterResponseDto> findAll() {
        List<RequesterResponseDto> requesters = new ArrayList<>();

        for (RequesterEntity requester : requesterRepository.findAll()) {
            requesters.add(requesterMapper.toResponseDto(requester));
        }

        return requesters;
    }

    @Override
    public RequesterResponseDto findById(Long id) {
        Optional<RequesterEntity> requesterOptional =
                requesterRepository.findById(id);

        if (requesterOptional.isEmpty()) {
            throw new RequesterNotFoundException(id);
        }

        return requesterMapper.toResponseDto(requesterOptional.get());
    }

    @Override
    public RequesterResponseDto save(
            RequesterRequestDto requesterRequestDto
    ) {
        RequesterEntity requester =
                requesterMapper.toEntity(requesterRequestDto);

        RequesterEntity savedRequester =
                requesterRepository.save(requester);

        return requesterMapper.toResponseDto(savedRequester);
    }
}*/