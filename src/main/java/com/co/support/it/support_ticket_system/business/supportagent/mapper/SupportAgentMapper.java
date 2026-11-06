package com.co.support.it.support_ticket_system.business.supportagent.mapper;

import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentRequestDto;
import com.co.support.it.support_ticket_system.business.supportagent.dto.SupportAgentResponseDto;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupportAgentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    SupportAgentEntity toEntity(SupportAgentRequestDto requestDto);

    SupportAgentResponseDto toResponseDto(SupportAgentEntity supportAgent);

    List<SupportAgentResponseDto> toResponseDtoList(
            List<SupportAgentEntity> supportAgents
    );
}