package com.co.support.it.support_ticket_system.repository;

import com.co.support.it.support_ticket_system.entity.TicketCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository
        extends JpaRepository<TicketCommentEntity, Long> {

    List<TicketCommentEntity> findByTicket_IdOrderByCreatedAtAsc(Long ticketId);
}