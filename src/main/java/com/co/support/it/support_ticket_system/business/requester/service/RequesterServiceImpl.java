package com.co.support.it.support_ticket_system.business.requester.service;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.mapper.RequesterMapper;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.generic.exception.DuplicateException;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RequesterServiceImpl
        extends GenericServiceImpl<RequesterEntity, RequesterRequestDto, RequesterResponseDto, Long>
        implements RequesterService {

    private final RequesterRepository requesterRepository;

    public RequesterServiceImpl(
            RequesterRepository requesterRepository,
            RequesterMapper requesterMapper
    ) {
        super(
                requesterRepository,
                requesterMapper::toEntity,
                requesterMapper::toResponseDto,
                requesterMapper::updateEntity
        );
        this.requesterRepository = requesterRepository;
    }

    @Override
    protected NotFoundException createNotFoundException(Long id) {
        return new RequesterNotFoundException(id);
    }

    @Override
    protected void validateUniqueness(RequesterRequestDto request, Long currentId) {
        boolean duplicated = currentId == null
                ? requesterRepository.existsByEmailIgnoreCase(request.email())
                : requesterRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), currentId);

        if (duplicated) {
            throw new DuplicateException(
                    "Ya existe un registro de solicitante con el correo: " + request.email()
            );
        }
    }
}
