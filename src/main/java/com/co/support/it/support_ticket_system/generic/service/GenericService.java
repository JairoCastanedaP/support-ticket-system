package com.co.support.it.support_ticket_system.generic.service;

import java.util.List;

public interface GenericService<REQUEST, RESPONSE, ID> {

    List<RESPONSE> findAll();

    RESPONSE findById(ID id);

    RESPONSE save(REQUEST request);
}