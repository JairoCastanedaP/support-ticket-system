package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {


}
