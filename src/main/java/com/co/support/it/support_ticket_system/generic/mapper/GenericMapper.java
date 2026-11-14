package com.co.support.it.support_ticket_system.generic.mapper;

import com.co.support.it.support_ticket_system.generic.dto.AuditResponseDto;
import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;

/**
 * Métodos de mapeo compartidos por los mappers de entidades que heredan de {@link GenericEntity}.
 */
public interface GenericMapper {

    default AuditResponseDto toAudit(GenericEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AuditResponseDto(
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}
