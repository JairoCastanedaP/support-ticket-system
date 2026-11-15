-- =====================================================================
-- Objetos de base de datos de MySQL que no gestiona Hibernate.
-- Las tablas las crea Hibernate (ddl-auto: update); este script se ejecuta
-- después y se puede repetir sin error en cada arranque.
-- Separador de sentencias: $$ (spring.sql.init.separator).
-- =====================================================================

-- Vista de solo lectura con el resumen de cada ticket activo (VO: TicketSummaryView).
CREATE OR REPLACE VIEW vw_ticket_summary AS
SELECT t.ticket_id,
       t.ticket_number,
       t.title,
       t.status,
       t.priority,
       c.name                                                        AS category_name,
       CONCAT(r.name, ' ', r.surname)                                AS requester_full_name,
       r.email                                                       AS requester_email,
       CASE WHEN a.support_agent_id IS NULL THEN NULL
            ELSE CONCAT(a.name, ' ', a.surname) END                  AS agent_full_name,
       (SELECT COUNT(*) FROM ticket_comments tc
         WHERE tc.ticket_id = t.ticket_id)                           AS comment_count,
       t.created_at,
       t.resolved_at,
       t.closed_at,
       TIMESTAMPDIFF(HOUR, t.created_at,
                     COALESCE(t.resolved_at, t.closed_at, NOW()))    AS age_hours
FROM tickets t
         JOIN categories c ON c.category_id = t.category_id
         JOIN requesters r ON r.requester_id = t.requester_id
         LEFT JOIN support_agents a ON a.support_agent_id = t.assigned_agent_id
WHERE t.is_active = TRUE
  AND t.deleted_at IS NULL
$$

-- Indicadores de tickets activos (RF-10).
-- Resultado: una fila por dimensión (STATUS o CATEGORY), elemento y cantidad.
-- Parámetro de salida: horas promedio entre la creación y la resolución.
-- READS SQL DATA declara que solo lee; así el driver permite llamarlo en transacciones de solo lectura.
DROP PROCEDURE IF EXISTS usp_ticket_indicators
$$

CREATE PROCEDURE usp_ticket_indicators(OUT p_average_resolution_hours DECIMAL(10, 2))
    READS SQL DATA
BEGIN
    SELECT ROUND(AVG(TIMESTAMPDIFF(MINUTE, created_at, resolved_at)) / 60, 2)
      INTO p_average_resolution_hours
      FROM tickets
     WHERE is_active = TRUE
       AND deleted_at IS NULL
       AND resolved_at IS NOT NULL;

    SELECT 'STATUS' AS dimension, t.status AS item, COUNT(*) AS total
      FROM tickets t
     WHERE t.is_active = TRUE
       AND t.deleted_at IS NULL
     GROUP BY t.status
    UNION ALL
    SELECT 'CATEGORY' AS dimension, c.name AS item, COUNT(*) AS total
      FROM tickets t
           JOIN categories c ON c.category_id = t.category_id
     WHERE t.is_active = TRUE
       AND t.deleted_at IS NULL
     GROUP BY c.name
     ORDER BY dimension DESC, item;
END
$$
