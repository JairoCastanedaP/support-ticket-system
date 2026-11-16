package com.co.support.it.support_ticket_system.business.batch.config;

import com.co.support.it.support_ticket_system.business.batch.job.*;
import com.co.support.it.support_ticket_system.business.batch.repository.AuditHistoryRepository;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.SimplePropertySqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.time.OffsetDateTime;

/**
 * Job de Spring Batch que copia la auditoría central (Oracle) al histórico (PostgreSQL).
 * Lector: eventos de audit_event desde una fecha → Procesador: descarta los ya copiados y clasifica →
 * Escritor: inserta en audit_event_history sin duplicar. Procesa por bloques del tamaño recibido como parámetro.
 */
@Configuration
public class AuditHistoryJobConfiguration {

    public static final String JOB_NAME = "auditHistoryJob";
    public static final String STEP_NAME = "copyAuditEvents";

    private static final String READ_SQL = """
            SELECT event_id, source_system, event_type, entity_type, entity_id, occurred_at_utc,
                   actor_id, client_ip, correlation_id, payload
              FROM audit_event
             WHERE occurred_at_utc >= ?
             ORDER BY occurred_at_utc, event_id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO audit_event_history (
                event_id, source_system, event_type, event_category, entity_type, entity_id, occurred_at_utc,
                actor_id, client_ip, correlation_id, http_method, http_path, http_status, duration_millis,
                ticket_number, previous_status, new_status, exported_at_utc, job_execution_id)
            VALUES (
                :eventId, :sourceSystem, :eventType, :eventCategory, :entityType, :entityId, :occurredAtUtc,
                :actorId, :clientIp, :correlationId, :httpMethod, :httpPath, :httpStatus, :durationMillis,
                :ticketNumber, :previousStatus, :newStatus, :exportedAtUtc, :jobExecutionId)
            ON CONFLICT (event_id) DO NOTHING
            """;

    /** Perfil local: H2 no tiene ON CONFLICT; MERGE ... KEY es su equivalente. */
    private static final String H2_MERGE_SQL = """
            MERGE INTO audit_event_history (
                event_id, source_system, event_type, event_category, entity_type, entity_id, occurred_at_utc,
                actor_id, client_ip, correlation_id, http_method, http_path, http_status, duration_millis,
                ticket_number, previous_status, new_status, exported_at_utc, job_execution_id)
            KEY (event_id)
            VALUES (
                :eventId, :sourceSystem, :eventType, :eventCategory, :entityType, :entityId, :occurredAtUtc,
                :actorId, :clientIp, :correlationId, :httpMethod, :httpPath, :httpStatus, :durationMillis,
                :ticketNumber, :previousStatus, :newStatus, :exportedAtUtc, :jobExecutionId)
            """;

    @Bean
    public AuditHistoryJobLogListener auditHistoryJobLogListener() {
        return new AuditHistoryJobLogListener();
    }

    @Bean
    public Job auditHistoryJob(JobRepository jobRepository, Step copyAuditEventsStep,
                               AuditHistoryJobLogListener logListener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .listener(logListener)
                .start(copyAuditEventsStep)
                .build();
    }

    /**
     * El paso se crea en cada ejecución (@JobScope) porque el tamaño de bloque llega como parámetro del job.
     */
    @Bean
    @JobScope
    public Step copyAuditEventsStep(
            JobRepository jobRepository,
            @Qualifier("historyTransactionManager") PlatformTransactionManager transactionManager,
            @Value("#{jobParameters['chunkSize']}") Long chunkSize,
            JdbcCursorItemReader<AuditEventRecord> auditEventReader,
            AuditHistoryItemProcessor auditHistoryItemProcessor,
            JdbcBatchItemWriter<AuditHistoryRecord> auditHistoryWriter,
            AuditHistoryJobLogListener logListener
    ) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<AuditEventRecord, AuditHistoryRecord>chunk(chunkSize.intValue())
                .transactionManager(transactionManager)
                .reader(auditEventReader)
                .processor(auditHistoryItemProcessor)
                .writer(auditHistoryWriter)
                .listener(auditHistoryItemProcessor)
                .listener(logListener)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<AuditEventRecord> auditEventReader(
            @Qualifier("oracleAuditDataSource") DataSource auditDataSource,
            @Value("#{jobParameters['fromOccurredAt']}") String fromOccurredAt,
            @Value("#{jobParameters['chunkSize']}") Long chunkSize
    ) {
        OffsetDateTime from = OffsetDateTime.parse(fromOccurredAt);

        return new JdbcCursorItemReaderBuilder<AuditEventRecord>()
                .name("auditEventReader")
                .dataSource(auditDataSource)
                .sql(READ_SQL)
                .preparedStatementSetter(statement -> statement.setObject(1, from))
                .fetchSize(chunkSize.intValue())
                .rowMapper((rs, rowNum) -> new AuditEventRecord(
                        rs.getString("event_id"),
                        rs.getString("source_system"),
                        rs.getString("event_type"),
                        rs.getString("entity_type"),
                        rs.getString("entity_id"),
                        rs.getObject("occurred_at_utc", OffsetDateTime.class).toInstant(),
                        rs.getString("actor_id"),
                        rs.getString("client_ip"),
                        rs.getString("correlation_id"),
                        rs.getString("payload")))
                .build();
    }

    @Bean
    @StepScope
    public AuditHistoryItemProcessor auditHistoryItemProcessor(
            AuditHistoryRepository historyRepository,
            JsonMapper jsonMapper,
            @Value("#{jobParameters['fromOccurredAt']}") String fromOccurredAt
    ) {
        return new AuditHistoryItemProcessor(historyRepository, jsonMapper, OffsetDateTime.parse(fromOccurredAt));
    }

    /**
     * Inserta sin duplicar: si el evento ya existe (event_id), no hace nada.
     */
    @Bean
    public JdbcBatchItemWriter<AuditHistoryRecord> auditHistoryWriter(@Qualifier("historyDataSource") DataSource historyDataSource)
            throws MetaDataAccessException {
        String product = JdbcUtils.extractDatabaseMetaData(historyDataSource, DatabaseMetaData::getDatabaseProductName);

        return new JdbcBatchItemWriterBuilder<AuditHistoryRecord>()
                .dataSource(historyDataSource)
                .sql("H2".equalsIgnoreCase(product) ? H2_MERGE_SQL : INSERT_SQL)
                .itemSqlParameterSourceProvider(SimplePropertySqlParameterSource::new)
                .assertUpdates(false)
                .build();
    }
}
