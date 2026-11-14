package com.co.support.it.support_ticket_system.business.ticket.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class TicketStatusTest {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED = Map.of(
            OPEN, EnumSet.of(IN_PROGRESS, CLOSED),
            IN_PROGRESS, EnumSet.of(WAITING_FOR_REQUESTER, RESOLVED),
            WAITING_FOR_REQUESTER, EnumSet.of(IN_PROGRESS, CLOSED),
            RESOLVED, EnumSet.of(CLOSED, IN_PROGRESS),
            CLOSED, EnumSet.noneOf(TicketStatus.class)
    );

    /** Recorre las 25 combinaciones origen → destino. */
    static Stream<Arguments> allTransitions() {
        return Stream.of(values()).flatMap(from ->
                Stream.of(values()).map(to -> Arguments.of(from, to, ALLOWED.get(from).contains(to))));
    }

    @ParameterizedTest(name = "{0} -> {1} permitido: {2}")
    @MethodSource("allTransitions")
    void canTransitionTo_respectsTheTransitionTable(TicketStatus from, TicketStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }

    @Test
    void closedIsTerminal() {
        assertThat(CLOSED.allowedTransitions()).isEmpty();
    }

    @Test
    void inProgressCannotGoBackToOpen() {
        assertThat(IN_PROGRESS.canTransitionTo(OPEN)).isFalse();
    }

    @Test
    void nullTargetIsNotAllowed() {
        assertThat(OPEN.canTransitionTo(null)).isFalse();
    }
}
