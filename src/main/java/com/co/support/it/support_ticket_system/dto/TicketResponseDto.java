package com.co.support.it.support_ticket_system.dto;

import java.time.LocalDateTime;

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
        String priority,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}