package com.co.support.it.support_ticket_system.business.supportagent.repository;

import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportAgentRepository extends JpaRepository<SupportAgentEntity, Long> {
}
