package com.co.support.it.support_ticket_system.business.category.dto;

import com.co.support.it.support_ticket_system.generic.dto.AuditResponseDto;
import com.co.support.it.support_ticket_system.generic.dto.IdentifiableDto;

public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        boolean active,
        AuditResponseDto audit
) implements IdentifiableDto<Long> {
}
