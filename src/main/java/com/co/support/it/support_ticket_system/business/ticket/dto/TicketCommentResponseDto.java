package com.co.support.it.support_ticket_system.dto;

import java.time.LocalDateTime;

public record TicketCommentResponseDto(
        Long id,
        Long ticketId,
        String comment,
        LocalDateTime createdAt
) {
}