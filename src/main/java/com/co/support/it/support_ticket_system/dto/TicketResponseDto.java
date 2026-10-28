package com.co.support.it.support_ticket_system.dto;

public record TicketResponseDto(
        Long id,
        String title,
        String description,
        Long requesterId,
        String requesterName,
        String requesterSurname,
        String requesterEmail,
        String requesterPhoneNumber,
        Long categoryId,
        String categoryName,
        String priority,
        String status
) {
}