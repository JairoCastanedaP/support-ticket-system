package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.dto.TicketCommentResponseDto;

import java.util.List;

public interface TicketCommentService {

    List<TicketCommentResponseDto> findByTicketId(Long ticketId);

    TicketCommentResponseDto save(
            Long ticketId,
            TicketCommentRequestDto requestDto
    );
}