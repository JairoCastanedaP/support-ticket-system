package com.co.support.it.support_ticket_system.generic.service;

import java.util.List;

/**
 * Contrato CRUD genérico parametrizado por DTO de entrada, DTO de salida e identificador.
 */
public interface GenericService<REQUEST, RESPONSE, ID> {

    /** Lista los registros activos. */
    List<RESPONSE> findAll();

    /** Consulta un registro, activo o no. */
    RESPONSE findById(ID id);

    RESPONSE save(REQUEST request);

    RESPONSE update(ID id, REQUEST request);

    /** Eliminación lógica: marca el registro como inactivo. */
    void delete(ID id);
}
