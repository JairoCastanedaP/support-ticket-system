package com.co.support.it.support_ticket_system.business.ticket.service.validation;

import com.co.support.it.support_ticket_system.business.category.entity.CategoryEntity;
import com.co.support.it.support_ticket_system.business.category.repository.CategoryRepository;
import com.co.support.it.support_ticket_system.business.requester.entity.RequesterEntity;
import com.co.support.it.support_ticket_system.business.requester.exception.RequesterNotFoundException;
import com.co.support.it.support_ticket_system.business.requester.repository.RequesterRepository;
import com.co.support.it.support_ticket_system.business.supportagent.entity.SupportAgentEntity;
import com.co.support.it.support_ticket_system.business.supportagent.repository.SupportAgentRepository;
import com.co.support.it.support_ticket_system.business.ticket.domain.TicketPriority;
import com.co.support.it.support_ticket_system.business.ticket.dto.TicketRequestDto;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import com.co.support.it.support_ticket_system.generic.entity.GenericEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketValidationChainTest {

    private RequesterRepository requesterRepository;
    private CategoryRepository categoryRepository;
    private SupportAgentRepository supportAgentRepository;
    private TicketValidationChain chain;

    @BeforeEach
    void setUp() {
        requesterRepository = mock(RequesterRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        supportAgentRepository = mock(SupportAgentRepository.class);

        chain = new TicketValidationChain(
                new RequesterValidationHandler(requesterRepository),
                new CategoryValidationHandler(categoryRepository),
                new SupportAgentValidationHandler(supportAgentRepository)
        );
    }

    private static TicketRequestDto request(Long agentId) {
        return new TicketRequestDto("Sin red", "No hay conexión", 1L, 2L, agentId, TicketPriority.HIGH);
    }

    private static <T extends GenericEntity> T active(T entity, boolean active) {
        entity.setActive(active);
        return entity;
    }

    @Test
    void validRequest_passesTheWholeChainAndFillsTheContext() {
        when(requesterRepository.findById(1L)).thenReturn(Optional.of(active(new RequesterEntity(), true)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(active(new CategoryEntity(), true)));
        when(supportAgentRepository.findById(3L)).thenReturn(Optional.of(active(new SupportAgentEntity(), true)));

        TicketValidationContext context = chain.validate(request(3L));

        assertThat(context.getRequester()).isNotNull();
        assertThat(context.getCategory()).isNotNull();
        assertThat(context.getSupportAgent()).isNotNull();
    }

    @Test
    void missingRequester_stopsTheChainAtTheFirstLink() {
        when(requesterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chain.validate(request(null)))
                .isInstanceOf(RequesterNotFoundException.class);

        verifyNoInteractions(categoryRepository, supportAgentRepository);
    }

    @Test
    void inactiveCategory_isRejectedAndTheAgentIsNotChecked() {
        when(requesterRepository.findById(1L)).thenReturn(Optional.of(active(new RequesterEntity(), true)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(active(new CategoryEntity(), false)));

        assertThatThrownBy(() -> chain.validate(request(3L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("categoría");

        verifyNoInteractions(supportAgentRepository);
    }

    @Test
    void inactiveAgent_isRejected() {
        when(requesterRepository.findById(1L)).thenReturn(Optional.of(active(new RequesterEntity(), true)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(active(new CategoryEntity(), true)));
        when(supportAgentRepository.findById(3L)).thenReturn(Optional.of(active(new SupportAgentEntity(), false)));

        assertThatThrownBy(() -> chain.validate(request(3L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("agente");
    }

    @Test
    void withoutAgent_theLastLinkIsSkipped() {
        when(requesterRepository.findById(1L)).thenReturn(Optional.of(active(new RequesterEntity(), true)));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(active(new CategoryEntity(), true)));

        TicketValidationContext context = chain.validate(request(null));

        assertThat(context.getSupportAgent()).isNull();
        verify(supportAgentRepository, never()).findById(any());
    }
}
