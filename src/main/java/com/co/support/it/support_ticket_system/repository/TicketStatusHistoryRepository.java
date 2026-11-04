package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.TicketStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketStatusHistoryRepository
        extends JpaRepository<TicketStatusHistoryEntity, Long> {

    List<TicketStatusHistoryEntity> findByTicket_IdOrderByChangedAtAsc(Long ticketId);
}