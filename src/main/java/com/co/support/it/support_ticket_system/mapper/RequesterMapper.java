package com.co.support.it.support_ticket_system.mapper;

import com.co.support.it.support_ticket_system.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.entity.RequesterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequesterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    RequesterEntity toEntity(RequesterRequestDto requesterRequestDto);

    RequesterResponseDto toResponseDto(RequesterEntity requesterEntity);
}