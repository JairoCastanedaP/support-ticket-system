package com.co.support.it.support_ticket_system.generic.dto;

/**
 * DTO de respuesta que expone su identificador; permite construir la URI del recurso creado.
 */
public interface IdentifiableDto<ID> {

    ID id();
}
