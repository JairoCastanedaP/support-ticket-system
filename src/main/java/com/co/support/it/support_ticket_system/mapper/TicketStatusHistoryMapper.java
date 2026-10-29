package com.co.support.it.support_ticket_system.mapper;

import com.co.support.it.support_ticket_system.dto.TicketStatusHistoryResponseDto;
import com.co.support.it.support_ticket_system.entity.TicketStatusHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketStatusHistoryMapper {

    @Mapping(target = "ticketId", source = "ticket.id")
    TicketStatusHistoryResponseDto toResponseDto(
            TicketStatusHistoryEntity history
    );

    List<TicketStatusHistoryResponseDto> toResponseDtoList(
            List<TicketStatusHistoryEntity> history
    );
}