package com.co.support.it.support_ticket_system.service;


import com.co.support.it.support_ticket_system.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.mapper.RequesterMapper;
import com.co.support.it.support_ticket_system.repository.RequesterRepository;
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
}