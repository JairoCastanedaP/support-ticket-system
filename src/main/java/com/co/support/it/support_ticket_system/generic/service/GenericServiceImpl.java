package com.co.support.it.support_ticket_system.generic.service;

import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import com.co.support.it.support_ticket_system.generic.exception.NotFoundException;
import com.co.support.it.support_ticket_system.generic.repository.GenericRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implementación CRUD reutilizable. Recibe el comportamiento variable como interfaces funcionales:
 * <ul>
 *     <li>{@code entityMapper}: DTO de entrada → entidad nueva.</li>
 *     <li>{@code responseMapper}: entidad → DTO de salida.</li>
 *     <li>{@code entityUpdater}: copia el DTO de entrada sobre una entidad existente.</li>
 * </ul>
 * Cada servicio especializado solo define cómo se construye su excepción de "no encontrado"
 * y, si aplica, sus reglas de unicidad.
 */
@Transactional(readOnly = true)
public abstract class GenericServiceImpl<ENTITY extends GenericEntity, REQUEST, RESPONSE, ID>
        implements GenericService<REQUEST, RESPONSE, ID> {

    private final GenericRepository<ENTITY, ID> repository;
    private final Function<REQUEST, ENTITY> entityMapper;
    private final Function<ENTITY, RESPONSE> responseMapper;
    private final BiConsumer<REQUEST, ENTITY> entityUpdater;

    protected GenericServiceImpl(
            GenericRepository<ENTITY, ID> repository,
            Function<REQUEST, ENTITY> entityMapper,
            Function<ENTITY, RESPONSE> responseMapper,
            BiConsumer<REQUEST, ENTITY> entityUpdater
    ) {
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.responseMapper = responseMapper;
        this.entityUpdater = entityUpdater;
    }

    protected abstract NotFoundException createNotFoundException(ID id);

    /**
     * Valida las reglas de unicidad antes de crear o actualizar.
     *
     * @param request   datos recibidos
     * @param currentId id del registro que se actualiza, o {@code null} al crear
     */
    protected void validateUniqueness(REQUEST request, ID currentId) {
        // Sin reglas de unicidad por defecto.
    }

    @Override
    public List<RESPONSE> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(responseMapper)
                .toList();
    }

    @Override
    public RESPONSE findById(ID id) {
        return responseMapper.apply(findEntity(id));
    }

    @Override
    @Transactional
    public RESPONSE save(REQUEST request) {
        validateUniqueness(request, null);
        ENTITY entity = entityMapper.apply(request);
        return responseMapper.apply(repository.save(entity));
    }

    @Override
    @Transactional
    public RESPONSE update(ID id, REQUEST request) {
        ENTITY entity = findEntity(id);
        validateUniqueness(request, id);
        entityUpdater.accept(request, entity);
        return responseMapper.apply(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(ID id) {
        ENTITY entity = findEntity(id);
        entity.setActive(false);
        repository.save(entity);
    }

    protected ENTITY findEntity(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> createNotFoundException(id));
    }
}
