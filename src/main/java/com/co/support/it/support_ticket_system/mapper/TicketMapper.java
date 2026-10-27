package com.co.support.it.support_ticket_system.mapper;


import com.co.support.it.support_ticket_system.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    TicketEntity toEntity(TicketRequestDto ticketRequestDto);

    TicketResponseDto toResponseDto(TicketEntity ticketEntity);

    List<TicketResponseDto> toResponseDtoList(List<TicketEntity> ticketEntities);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TicketRequestDto ticketRequestDto, @MappingTarget TicketEntity ticketEntity);
}
