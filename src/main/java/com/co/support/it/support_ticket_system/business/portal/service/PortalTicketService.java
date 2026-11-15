package com.co.support.it.support_ticket_system.business.portal.service;

import com.co.support.it.support_ticket_system.business.portal.dto.PortalTicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta de tickets para el portal de clientes externos.
 */
@Service
@Transactional(readOnly = true)
public class PortalTicketService {

    private final TicketRepository ticketRepository;

    public PortalTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public PortalTicketResponseDto findByTicketNumber(String ticketNumber) {
        TicketEntity ticket = ticketRepository.findActiveByTicketNumber(ticketNumber)
                .orElseThrow(() -> new TicketNotFoundException(ticketNumber));

        return new PortalTicketResponseDto(
                ticket.getTicketNumber(),
                ticket.getTitle(),
                ticket.getStatus(),
                ticket.getCategory().getName(),
                ticket.getCreatedAt(),
                ticket.getResolvedAt(),
                ticket.getClosedAt(),
                ticket.getResolutionNote()
        );
    }
}
