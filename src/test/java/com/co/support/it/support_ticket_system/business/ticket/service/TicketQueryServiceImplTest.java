package com.co.support.it.support_ticket_system.business.ticket.service;

import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketMapper;
import com.co.support.it.support_ticket_system.business.ticket.mapper.TicketQueryMapper;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.IndicatorRow;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketIndicatorsRepository.TicketIndicatorsResult;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketRepository;
import com.co.support.it.support_ticket_system.business.ticket.repository.TicketSummaryViewRepository;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketIndicatorsResponseDto;
import com.co.support.it.support_ticket_system.business.ticket.exception.TicketNotFoundException;
import com.co.support.it.support_ticket_system.generic.exception.BadRequestException;
import com.co.support.it.support_ticket_system.generic.exception.FeatureNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TicketQueryServiceImplTest {

    private TicketRepository ticketRepository;
    private TicketIndicatorsRepository indicatorsRepository;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        indicatorsRepository = mock(TicketIndicatorsRepository.class);
    }

    private TicketQueryServiceImpl service(boolean storedProceduresEnabled) {
        return new TicketQueryServiceImpl(
                ticketRepository,
                mock(TicketSummaryViewRepository.class),
                indicatorsRepository,
                mock(TicketMapper.class),
                mock(TicketQueryMapper.class),
                storedProceduresEnabled
        );
    }

    @Test
    void indicators_groupRowsByDimensionAndFillMissingStatusesWithZero() {
        when(indicatorsRepository.callIndicatorsProcedure()).thenReturn(new TicketIndicatorsResult(
                List.of(
                        new IndicatorRow("STATUS", "OPEN", 3),
                        new IndicatorRow("STATUS", "CLOSED", 2),
                        new IndicatorRow("CATEGORY", "Hardware", 4),
                        new IndicatorRow("CATEGORY", "Redes", 1)
                ),
                new BigDecimal("5.25")
        ));

        TicketIndicatorsResponseDto indicators = service(true).getIndicators();

        assertThat(indicators.totalTickets()).isEqualTo(5);
        assertThat(indicators.byStatus()).containsExactly(
                entry("OPEN", 3L), entry("IN_PROGRESS", 0L), entry("WAITING_FOR_REQUESTER", 0L),
                entry("RESOLVED", 0L), entry("CLOSED", 2L));
        assertThat(indicators.byCategory()).containsExactly(entry("Hardware", 4L), entry("Redes", 1L));
        assertThat(indicators.averageResolutionHours()).isEqualByComparingTo("5.25");
    }

    @Test
    void indicators_withoutStoredProcedures_areNotAvailable() {
        assertThatThrownBy(() -> service(false).getIndicators())
                .isInstanceOf(FeatureNotAvailableException.class);

        verifyNoInteractions(indicatorsRepository);
    }

    @Test
    void search_withShortText_isBadRequest() {
        assertThatThrownBy(() -> service(true).search(" ab "))
                .isInstanceOf(BadRequestException.class);

        verify(ticketRepository, never()).searchByText(anyString());
    }

    @Test
    void search_trimsTheText() {
        when(ticketRepository.searchByText("red")).thenReturn(List.of());

        assertThat(service(true).search("  red  ")).isEmpty();
        verify(ticketRepository).searchByText("red");
    }

    @Test
    void findByTicketNumber_notFound() {
        when(ticketRepository.findActiveByTicketNumber("TCK-2026-999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(true).findByTicketNumber("TCK-2026-999999"))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("TCK-2026-999999");
    }

    private static java.util.Map.Entry<String, Long> entry(String key, Long value) {
        return java.util.Map.entry(key, value);
    }
}
