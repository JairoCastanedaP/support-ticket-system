package com.co.support.it.support_ticket_system.business.ticket.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ejecuta el procedimiento almacenado {@code usp_ticket_indicators} de MySQL.
 */
@Repository
public class TicketIndicatorsRepository {

    private static final String PROCEDURE = "usp_ticket_indicators";

    @PersistenceContext
    private EntityManager entityManager;

    public TicketIndicatorsResult callIndicatorsProcedure() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(PROCEDURE);
        query.registerStoredProcedureParameter(1, BigDecimal.class, ParameterMode.OUT);
        query.execute();

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<IndicatorRow> indicators = rows.stream()
                .map(row -> new IndicatorRow(
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        ((Number) row[2]).longValue()))
                .toList();

        return new TicketIndicatorsResult(indicators, (BigDecimal) query.getOutputParameterValue(1));
    }

    /** Una fila del procedimiento: dimensión (STATUS o CATEGORY), elemento y cantidad. */
    public record IndicatorRow(String dimension, String item, long total) {
    }

    public record TicketIndicatorsResult(List<IndicatorRow> rows, BigDecimal averageResolutionHours) {
    }
}
