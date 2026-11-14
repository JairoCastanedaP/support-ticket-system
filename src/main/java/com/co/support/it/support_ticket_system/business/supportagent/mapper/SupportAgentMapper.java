package com.co.support.it.support_ticket_system.business.supportagent.mapper;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.generic.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupportAgentMapper extends GenericMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SupportAgentEntity toEntity(SupportAgentRequestDto requestDto);

    @Mapping(target = "audit", source = "entity")
    SupportAgentResponseDto toResponseDto(SupportAgentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(SupportAgentRequestDto requestDto, @MappingTarget SupportAgentEntity entity);
}
