package com.co.support.it.support_ticket_system.business.ticket.mapper;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSearchResultDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketSummaryResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketSummaryView;
import com.co.support.it.support_ticket_system.business.ticket.projection.TicketSearchProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketQueryMapper {

    TicketSummaryResponseDto toSummaryDto(TicketSummaryView view);

    TicketSearchResultDto toSearchResultDto(TicketSearchProjection projection);
}
