package com.co.support.it.support_ticket_system.business.batch.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Consultas sobre la base histórica con JDBC (no necesita JPA: solo inserta y agrega).
 */
@Repository
public class AuditHistoryRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public AuditHistoryRepository(@Qualifier("historyDataSource") DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    /** Fecha del último evento copiado, o null si el histórico está vacío. */
    public Instant findLastOccurredAt() {
        OffsetDateTime last = jdbc.getJdbcTemplate().queryForObject(
                "SELECT MAX(occurred_at_utc) FROM audit_event_history", OffsetDateTime.class);
        return last == null ? null : last.toInstant();
    }

    /** Ids ya copiados desde una fecha: el procesador los descarta para no duplicarlos. */
    public Set<String> findEventIdsSince(OffsetDateTime from) {
        return new HashSet<>(jdbc.queryForList(
                "SELECT event_id FROM audit_event_history WHERE occurred_at_utc >= :from",
                new MapSqlParameterSource("from", from), String.class));
    }

    public long count() {
        Long total = jdbc.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM audit_event_history", Long.class);
        return total == null ? 0 : total;
    }

    public Map<String, Long> countBy(String column) {
        Map<String, Long> result = new LinkedHashMap<>();
        // column es una constante interna (event_type o event_category), nunca un dato del usuario.
        jdbc.getJdbcTemplate().query(
                "SELECT " + column + " AS item, COUNT(*) AS total FROM audit_event_history GROUP BY " + column
                        + " ORDER BY total DESC, item",
                rs -> {
                    result.put(rs.getString("item"), rs.getLong("total"));
                });
        return result;
    }
}
