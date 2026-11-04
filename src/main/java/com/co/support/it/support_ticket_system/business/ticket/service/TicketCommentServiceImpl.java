package com.co.support.it.support_ticket_system.service;

import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketCommentResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketCommentEntity;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.mapper.TicketCommentMapper;
import com.co.support.it.support_ticket_system.repository.TicketCommentRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final TicketCommentMapper ticketCommentMapper;

    public TicketCommentServiceImpl(
            TicketCommentRepository ticketCommentRepository,
            TicketRepository ticketRepository,
            TicketCommentMapper ticketCommentMapper
    ) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketRepository = ticketRepository;
        this.ticketCommentMapper = ticketCommentMapper;
    }

    @Override
    public List<TicketCommentResponseDto> findByTicketId(Long ticketId) {
        ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        return ticketCommentMapper.toResponseDtoList(
                ticketCommentRepository.findByTicket_IdOrderByCreatedAtAsc(ticketId)
        );
    }

    @Override
    @Transactional
    public TicketCommentResponseDto save(
            Long ticketId,
            TicketCommentRequestDto requestDto
    ) {
        TicketEntity ticket = ticketRepository.findByIdAndActiveTrueAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        TicketCommentEntity comment = ticketCommentMapper.toEntity(requestDto);
        comment.setTicket(ticket);
        comment.setCreatedAt(LocalDateTime.now());

        TicketCommentEntity savedComment = ticketCommentRepository.save(comment);
        return ticketCommentMapper.toResponseDto(savedComment);
    }
}