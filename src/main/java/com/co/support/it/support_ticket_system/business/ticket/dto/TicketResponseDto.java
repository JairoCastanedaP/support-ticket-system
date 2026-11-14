package com.co.support.it.support_ticket_system.business.ticket.dto;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.generic.dto.AuditResponseDto;
import com.co.support.it.support_ticket_system.generic.dto.IdentifiableDto;

public record TicketResponseDto(
        Long id,
        String ticketNumber,
        String title,
        String description,
        Long requesterId,
        String requesterName,
        String requesterSurname,
        String requesterEmail,
        String requesterPhoneNumber,
        Long categoryId,
        String categoryName,
        Long supportAgentId,
        String supportAgentName,
        String supportAgentSurname,
        TicketPriority priority,
        TicketStatus status,
        AuditResponseDto audit
) implements IdentifiableDto<Long> {
}