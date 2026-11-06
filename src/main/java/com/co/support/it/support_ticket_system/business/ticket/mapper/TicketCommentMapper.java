package com.co.support.it.support_ticket_system.business.ticket.mapper;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketCommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketCommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TicketCommentEntity toEntity(TicketCommentRequestDto requestDto);

    @Mapping(target = "ticketId", source = "ticket.id")
    TicketCommentResponseDto toResponseDto(TicketCommentEntity comment);

    List<TicketCommentResponseDto> toResponseDtoList(
            List<TicketCommentEntity> comments
    );
}