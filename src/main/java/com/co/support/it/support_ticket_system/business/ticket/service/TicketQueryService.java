package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Consultas de solo lectura sobre tickets: búsquedas, listado resumido e indicadores.
 */
public interface TicketQueryService {

    TicketResponseDto findByTicketNumber(String ticketNumber);

    List<TicketSearchResultDto> search(String text);

    Page<TicketSummaryResponseDto> findSummary(TicketStatus status, TicketPriority priority, Pageable pageable);

    TicketIndicatorsResponseDto getIndicators();
}
