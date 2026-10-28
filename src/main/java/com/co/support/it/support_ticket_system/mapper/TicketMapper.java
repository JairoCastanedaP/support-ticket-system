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
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "assignedAgent", ignore = true)
    TicketEntity toEntity(TicketRequestDto ticketRequestDto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterName", source = "requester.name")
    @Mapping(target = "requesterSurname", source = "requester.surname")
    @Mapping(target = "requesterEmail", source = "requester.email")
    @Mapping(target = "requesterPhoneNumber", source = "requester.phoneNumber")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    TicketResponseDto toResponseDto(TicketEntity ticketEntity);

    List<TicketResponseDto> toResponseDtoList(List<TicketEntity> ticketEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "assignedAgent", ignore = true)
    void updateEntityFromDto(
            TicketRequestDto ticketRequestDto,
            @MappingTarget TicketEntity ticketEntity
    );
}