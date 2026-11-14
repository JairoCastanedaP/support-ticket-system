package com.co.support.it.support_ticket_system.business.ticket.service.status;

import com.co.support.it.support_ticket_system.business.ticket.domain.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Indexa por estado destino las estrategias que Spring encuentra como beans.
 * Agregar un estado nuevo solo requiere una nueva implementación de {@link TicketStatusChangeStrategy}.
 */
@Component
public class TicketStatusChangeStrategyResolver {

    private final Map<TicketStatus, TicketStatusChangeStrategy> strategies = new EnumMap<>(TicketStatus.class);

    public TicketStatusChangeStrategyResolver(List<TicketStatusChangeStrategy> strategies) {
        strategies.forEach(strategy -> this.strategies.put(strategy.targetStatus(), strategy));
    }

    public TicketStatusChangeStrategy resolve(TicketStatus target) {
        TicketStatusChangeStrategy strategy = strategies.get(target);
        if (strategy == null) {
            throw new IllegalStateException("No hay una estrategia configurada para el estado " + target);
        }
        return strategy;
    }
}
