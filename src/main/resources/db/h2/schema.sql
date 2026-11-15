-- =====================================================================
-- Perfil local (H2): misma vista que en MySQL con funciones de H2.
-- H2 no admite procedimientos con la sintaxis de MySQL; los indicadores
-- solo están disponibles con el perfil docker.
-- =====================================================================

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
       DATEDIFF('HOUR', t.created_at,
                COALESCE(t.resolved_at, t.closed_at, CURRENT_TIMESTAMP)) AS age_hours
FROM tickets t
         JOIN categories c ON c.category_id = t.category_id
         JOIN requesters r ON r.requester_id = t.requester_id
         LEFT JOIN support_agents a ON a.support_agent_id = t.assigned_agent_id
WHERE t.is_active = TRUE
  AND t.deleted_at IS NULL
$$
