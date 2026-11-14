package com.co.support.it.support_ticket_system.business.requester.dto;

import com.co.support.it.support_ticket_system.generic.dto.AuditResponseDto;
import com.co.support.it.support_ticket_system.generic.dto.IdentifiableDto;

public record RequesterResponseDto(
        Long id,
        String name,
        String surname,
        String email,
        String phoneNumber,
        boolean active,
        AuditResponseDto audit
) implements IdentifiableDto<Long> {
}
