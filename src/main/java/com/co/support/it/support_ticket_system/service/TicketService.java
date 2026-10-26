package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.entity.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<TicketEntity> findAll();
    Optional<TicketEntity> findById(Long id);
    TicketEntity save(TicketEntity ticketEntity);
    Optional<TicketEntity> update(Long id,TicketEntity ticketEntity);
}
