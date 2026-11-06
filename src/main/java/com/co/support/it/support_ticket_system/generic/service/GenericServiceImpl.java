package com.co.support.it.support_ticket_system.generic.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class GenericServiceImpl<ENTITY, REQUEST, RESPONSE, ID>
        implements GenericService<REQUEST, RESPONSE, ID> {

    private final JpaRepository<ENTITY, ID> repository;
    private final Function<REQUEST, ENTITY> entityMapper;
    private final Function<ENTITY, RESPONSE> responseMapper;

    protected GenericServiceImpl(
            JpaRepository<ENTITY, ID> repository,
            Function<REQUEST, ENTITY> entityMapper,
            Function<ENTITY, RESPONSE> responseMapper
    ) {
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.responseMapper = responseMapper;
    }

    protected abstract RuntimeException createNotFoundException(ID id);

    @Override
    @Transactional(readOnly = true)
    public List<RESPONSE> findAll() {
        List<RESPONSE> responses = new ArrayList<>();

        for (ENTITY entity : repository.findAll()) {
            responses.add(responseMapper.apply(entity));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public RESPONSE findById(ID id) {
        ENTITY entity = repository.findById(id)
                .orElseThrow(() -> createNotFoundException(id));

        return responseMapper.apply(entity);
    }

    @Override
    @Transactional
    public RESPONSE save(REQUEST request) {
        ENTITY entity = entityMapper.apply(request);
        ENTITY savedEntity = repository.save(entity);

        return responseMapper.apply(savedEntity);
    }
}