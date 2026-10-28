package com.co.support.it.support_ticket_system.dto;

public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        boolean status
) {
}