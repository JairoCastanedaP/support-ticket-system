package com.co.support.it.support_ticket_system.business.ticket.mapper;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.generic.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper extends GenericMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "assignedAgent", ignore = true)
    @Mapping(target = "ticketNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "resolutionNote", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    TicketEntity toEntity(TicketRequestDto ticketRequestDto);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "requesterName", source = "requester.name")
    @Mapping(target = "requesterSurname", source = "requester.surname")
    @Mapping(target = "requesterEmail", source = "requester.email")
    @Mapping(target = "requesterPhoneNumber", source = "requester.phoneNumber")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "supportAgentId", source = "assignedAgent.id")
    @Mapping(target = "supportAgentName", source = "assignedAgent.name")
    @Mapping(target = "supportAgentSurname", source = "assignedAgent.surname")
    @Mapping(target = "audit", source = "ticketEntity")
    TicketResponseDto toResponseDto(TicketEntity ticketEntity);

    List<TicketResponseDto> toResponseDtoList(List<TicketEntity> ticketEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "assignedAgent", ignore = true)
    @Mapping(target = "ticketNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "resolutionNote", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    void updateEntityFromDto(
            TicketRequestDto ticketRequestDto,
            @MappingTarget TicketEntity ticketEntity
    );
}