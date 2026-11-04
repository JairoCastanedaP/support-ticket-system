package com.co.support.it.support_ticket_system.business.category.dto;

public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        boolean status
) {
}