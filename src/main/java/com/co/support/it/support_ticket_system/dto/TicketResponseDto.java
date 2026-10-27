package com.co.support.it.support_ticket_system.dto;

public record TicketResponseDto(

        Long id,
        String title,
        String description,
        String requesterName,
        String requesterEmail,
        String priority,
        String status
) {
}
