package com.co.support.it.support_ticket_system.business.requester.repository;

import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.generic.repository.GenericRepository;

public interface RequesterRepository extends GenericRepository<RequesterEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
