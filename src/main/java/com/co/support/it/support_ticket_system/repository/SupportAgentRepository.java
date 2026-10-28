package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.SupportAgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportAgentRepository extends JpaRepository<SupportAgentEntity, Long> {
}
