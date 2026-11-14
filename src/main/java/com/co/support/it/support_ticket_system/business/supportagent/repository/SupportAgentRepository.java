package com.co.support.it.support_ticket_system.business.supportagent.repository;

import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.generic.repository.GenericRepository;

public interface SupportAgentRepository extends GenericRepository<SupportAgentEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
