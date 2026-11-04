package com.co.support.it.support_ticket_system.dto;


public record RequesterResponseDto(
        Long id,
        String name,
        String surname,
        String email,
        String phoneNumber,
        boolean active
) {
}