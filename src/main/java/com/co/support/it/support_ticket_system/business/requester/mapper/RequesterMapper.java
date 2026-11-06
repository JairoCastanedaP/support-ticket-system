package com.co.support.it.support_ticket_system.business.requester.mapper;

import com.co.support.it.support_ticket_system.business.requester.dto.RequesterRequestDto;
import com.co.support.it.support_ticket_system.business.requester.dto.RequesterResponseDto;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequesterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    RequesterEntity toEntity(RequesterRequestDto requesterRequestDto);

    RequesterResponseDto toResponseDto(RequesterEntity requesterEntity);
}