package com.co.support.it.support_ticket_system.business.ticket.repository;

import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TicketRepository
        extends JpaRepository<TicketEntity, Long>,
        JpaSpecificationExecutor<TicketEntity>
{

    Optional<TicketEntity> findByIdAndActiveTrueAndDeletedAtIsNull(Long id);

}
