package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketStatusChangeRequestDto;
import com.co.support.it.support_ticket_system.business.ticket.entity.TicketEntity;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketStatusChangeStrategyTest {

    private final InProgressStatusStrategy inProgress = new InProgressStatusStrategy();
    private final WaitingForRequesterStatusStrategy waiting = new WaitingForRequesterStatusStrategy();
    private final ResolvedStatusStrategy resolved = new ResolvedStatusStrategy();
    private final ClosedStatusStrategy closed = new ClosedStatusStrategy();

    private static TicketEntity ticket(TicketStatus status, boolean withAgent) {
        TicketEntity ticket = new TicketEntity();
        ticket.setStatus(status);
        if (withAgent) {
            ticket.setAssignedAgent(new SupportAgentEntity());
        }
        return ticket;
    }

    private static TicketStatusChangeRequestDto request(TicketStatus target, String reason) {
        return new TicketStatusChangeRequestDto(target, reason);
    }

    @Test
    void inProgress_withoutAgent_isRejected() {
        TicketEntity ticket = ticket(TicketStatus.OPEN, false);

        assertThatThrownBy(() -> inProgress.apply(ticket, request(TicketStatus.IN_PROGRESS, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("agente asignado");
    }

    @Test
    void inProgress_reopeningResolvedTicket_clearsResolution() {
        TicketEntity ticket = ticket(TicketStatus.RESOLVED, true);
        ticket.setResolutionNote("Se reinició el equipo");
        ticket.setResolvedAt(LocalDateTime.now());

        inProgress.apply(ticket, request(TicketStatus.IN_PROGRESS, "El problema volvió"));

        assertThat(ticket.getResolutionNote()).isNull();
        assertThat(ticket.getResolvedAt()).isNull();
    }

    @Test
    void waiting_hasNoAdditionalRules() {
        TicketEntity ticket = ticket(TicketStatus.IN_PROGRESS, true);

        waiting.apply(ticket, request(TicketStatus.WAITING_FOR_REQUESTER, null));

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void resolved_withoutNote_isBadRequest() {
        TicketEntity ticket = ticket(TicketStatus.IN_PROGRESS, true);

        assertThatThrownBy(() -> resolved.apply(ticket, request(TicketStatus.RESOLVED, "   ")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void resolved_withNote_storesNoteAndDate() {
        TicketEntity ticket = ticket(TicketStatus.IN_PROGRESS, true);

        resolved.apply(ticket, request(TicketStatus.RESOLVED, "  Se cambió el cable de red  "));

        assertThat(ticket.getResolutionNote()).isEqualTo("Se cambió el cable de red");
        assertThat(ticket.getResolvedAt()).isNotNull();
    }

    @Test
    void closed_withoutResolvingAndWithoutReason_isBadRequest() {
        TicketEntity ticket = ticket(TicketStatus.OPEN, false);

        assertThatThrownBy(() -> closed.apply(ticket, request(TicketStatus.CLOSED, null)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void closed_afterResolving_doesNotNeedReason() {
        TicketEntity ticket = ticket(TicketStatus.RESOLVED, true);

        closed.apply(ticket, request(TicketStatus.CLOSED, null));

        assertThat(ticket.getClosedAt()).isNotNull();
    }

    @Test
    void resolver_findsAStrategyForEveryReachableStatus() {
        TicketStatusChangeStrategyResolver resolver =
                new TicketStatusChangeStrategyResolver(List.of(inProgress, waiting, resolved, closed));

        assertThat(resolver.resolve(TicketStatus.IN_PROGRESS)).isSameAs(inProgress);
        assertThat(resolver.resolve(TicketStatus.WAITING_FOR_REQUESTER)).isSameAs(waiting);
        assertThat(resolver.resolve(TicketStatus.RESOLVED)).isSameAs(resolved);
        assertThat(resolver.resolve(TicketStatus.CLOSED)).isSameAs(closed);
    }
}
