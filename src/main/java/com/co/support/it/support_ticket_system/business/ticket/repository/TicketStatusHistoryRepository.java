package com.co.support.it.support_ticket_system.business.ticket.repository;

import com.co.support.it.support_ticket_system.business.ticket.entity.TicketStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketStatusHistoryRepository
        extends JpaRepository<TicketStatusHistoryEntity, Long> {

    List<TicketStatusHistoryEntity> findByTicket_IdOrderByChangedAtAsc(Long ticketId);
}