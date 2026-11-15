package com.co.support.it.support_ticket_system.business.ticket.repository;

import com.co.support.it.support_ticket_system.business.ticket.entity.TicketSummaryView;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Repositorio de solo lectura de la vista: no expone save ni delete.
 */
public interface TicketSummaryViewRepository
        extends Repository<TicketSummaryView, Long>, JpaSpecificationExecutor<TicketSummaryView> {

    Page<TicketSummaryView> findAll(Specification<TicketSummaryView> spec, Pageable pageable);
}
