-- =====================================================================
-- Base histórica (PostgreSQL; H2 en modo PostgreSQL en el perfil local).
-- Las tablas BATCH_* las crea Spring Batch; este script crea las propias.
-- Se ejecuta en cada arranque y se puede repetir sin error.
-- =====================================================================

-- Copia de los eventos de auditoría de Oracle, con sus datos principales ya extraídos.
CREATE TABLE IF NOT EXISTS audit_event_history (
    event_id          VARCHAR(36)              PRIMARY KEY,
    source_system     VARCHAR(80)              NOT NULL,
    event_type        VARCHAR(100)             NOT NULL,
    event_category    VARCHAR(30)              NOT NULL,
    entity_type       VARCHAR(80)              NOT NULL,
    entity_id         VARCHAR(100)             NOT NULL,
    occurred_at_utc   TIMESTAMP WITH TIME ZONE NOT NULL,
    actor_id          VARCHAR(100),
    client_ip         VARCHAR(45),
    correlation_id    VARCHAR(100),
    http_method       VARCHAR(10),
    http_path         VARCHAR(300),
    http_status       INTEGER,
    duration_millis   BIGINT,
    ticket_number     VARCHAR(40),
    previous_status   VARCHAR(30),
    new_status        VARCHAR(30),
    exported_at_utc   TIMESTAMP WITH TIME ZONE NOT NULL,
    job_execution_id  BIGINT                   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_event_history_occurred ON audit_event_history (occurred_at_utc);

-- Parámetros de los jobs: se leen en cada ejecución, así cambian sin reiniciar la aplicación.
CREATE TABLE IF NOT EXISTS batch_job_config (
    job_name     VARCHAR(100) PRIMARY KEY,
    chunk_size   INTEGER      NOT NULL CHECK (chunk_size BETWEEN 1 AND 1000),
    description  VARCHAR(250),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL
);

INSERT INTO batch_job_config (job_name, chunk_size, description, updated_at)
SELECT 'auditHistoryJob', 100, 'Volcado de la auditoría de Oracle al histórico', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM batch_job_config WHERE job_name = 'auditHistoryJob');
