package com.co.support.it.support_ticket_system.generic.dto;

import java.time.LocalDateTime;

/**
 * Datos de auditoría expuestos en las respuestas.
 */
public record AuditResponseDto(
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
}
