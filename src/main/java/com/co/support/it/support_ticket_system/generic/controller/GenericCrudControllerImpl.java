package com.co.support.it.support_ticket_system.generic.controller;

import com.co.support.it.support_ticket_system.generic.controller.response.CustomResponse;
import com.co.support.it.support_ticket_system.generic.dto.IdentifiableDto;
import com.co.support.it.support_ticket_system.generic.service.GenericService;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Implementación CRUD heredable por los controllers de catálogos.
 * Solo necesita el servicio y la ruta base del recurso.
 */
public abstract class GenericCrudControllerImpl<REQUEST, RESPONSE extends IdentifiableDto<ID>, ID>
        extends GenericControllerImpl
        implements GenericController<REQUEST, RESPONSE, ID> {

    private final GenericService<REQUEST, RESPONSE, ID> service;
    private final String basePath;

    protected GenericCrudControllerImpl(GenericService<REQUEST, RESPONSE, ID> service, String basePath) {
        this.service = service;
        this.basePath = basePath;
    }

    @Override
    public ResponseEntity<CustomResponse<List<RESPONSE>>> findAll() {
        return returnList(service.findAll());
    }

    @Override
    public ResponseEntity<CustomResponse<RESPONSE>> findById(ID id) {
        return returnOk(service.findById(id));
    }

    @Override
    public ResponseEntity<CustomResponse<RESPONSE>> save(REQUEST request) {
        RESPONSE saved = service.save(request);
        return returnCreated(basePath + "/" + saved.id(), saved);
    }

    @Override
    public ResponseEntity<CustomResponse<RESPONSE>> update(ID id, REQUEST request) {
        return returnUpdated(service.update(id, request));
    }

    @Override
    public ResponseEntity<CustomResponse<Void>> delete(ID id) {
        service.delete(id);
        return returnDeleted();
    }
}
