package com.co.support.it.support_ticket_system.business.batch.service;

import com.co.support.it.support_ticket_system.business.batch.config.AuditHistoryJobConfiguration;
import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistoryRunResponseDto;
import com.co.support.it.support_ticket_system.business.batch.dto.AuditHistorySummaryResponseDto;
import com.co.support.it.support_ticket_system.business.batch.repository.AuditHistoryRepository;
import com.co.support.it.support_ticket_system.business.batch.repository.BatchJobConfigRepository;
import com.co.support.it.support_ticket_system.business.shared.config.audit.JpaAuditingConfig;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lanza el volcado de auditoría de forma síncrona y arma el resumen del histórico.
 */
@Slf4j
@Service
public class AuditHistoryBatchService {

    static final int DEFAULT_CHUNK_SIZE = 100;

    private final JobOperator jobOperator;
    private final Job auditHistoryJob;
    private final AuditHistoryRepository historyRepository;
    private final BatchJobConfigRepository configRepository;
    private final AuditorAware<String> auditorProvider;
    private final Duration overlap;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public AuditHistoryBatchService(
            JobOperator jobOperator,
            @Qualifier("auditHistoryJob") Job auditHistoryJob,
            AuditHistoryRepository historyRepository,
            BatchJobConfigRepository configRepository,
            AuditorAware<String> auditorProvider,
            @Value("${app.batch.audit-history.overlap-minutes:10}") long overlapMinutes
    ) {
        this.jobOperator = jobOperator;
        this.auditHistoryJob = auditHistoryJob;
        this.historyRepository = historyRepository;
        this.configRepository = configRepository;
        this.auditorProvider = auditorProvider;
        this.overlap = Duration.ofMinutes(overlapMinutes);
    }

    /**
     * Ejecuta el job y espera a que termine. Solo se permite una ejecución a la vez (409).
     * Copia los eventos desde el último ya copiado, menos un margen por si el relay entregó eventos con retraso.
     */
    public AuditHistoryRunResponseDto run() {
        if (!running.compareAndSet(false, true)) {
            throw new BusinessRuleException("Ya hay un volcado del histórico de auditoría en curso");
        }
        try {
            JobParameters parameters = buildParameters();
            JobExecution execution = jobOperator.start(auditHistoryJob, parameters);
            return toResponse(execution, parameters);
        } catch (BusinessRuleException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo lanzar el volcado del histórico de auditoría", exception);
        } finally {
            running.set(false);
        }
    }

    public AuditHistorySummaryResponseDto summary() {
        return new AuditHistorySummaryResponseDto(
                historyRepository.count(),
                historyRepository.findLastOccurredAt(),
                historyRepository.countBy("event_category"),
                historyRepository.countBy("event_type")
        );
    }

    JobParameters buildParameters() {
        int chunkSize = configRepository.findChunkSize(AuditHistoryJobConfiguration.JOB_NAME)
                .orElseGet(() -> {
                    log.warn("No hay configuración para {} en batch_job_config; se usa el bloque por defecto ({})",
                            AuditHistoryJobConfiguration.JOB_NAME, DEFAULT_CHUNK_SIZE);
                    return DEFAULT_CHUNK_SIZE;
                });

        Instant last = historyRepository.findLastOccurredAt();
        OffsetDateTime from = (last == null ? Instant.EPOCH : last.minus(overlap)).atOffset(ZoneOffset.UTC);
        String requestedBy = auditorProvider.getCurrentAuditor().orElse(JpaAuditingConfig.SYSTEM_ACTOR);

        return new JobParametersBuilder()
                .addString("requestedBy", requestedBy)
                .addString("fromOccurredAt", from.toString())
                .addLong("chunkSize", (long) chunkSize)
                .addLong("requestedAt", System.currentTimeMillis())
                .toJobParameters();
    }

    private AuditHistoryRunResponseDto toResponse(JobExecution execution, JobParameters parameters) {
        StepExecution step = execution.getStepExecutions().stream().findFirst().orElse(null);
        LocalDateTime start = execution.getStartTime();
        LocalDateTime end = execution.getEndTime() != null ? execution.getEndTime() : LocalDateTime.now();

        return new AuditHistoryRunResponseDto(
                execution.getId(),
                execution.getStatus().name(),
                parameters.getString("requestedBy"),
                OffsetDateTime.parse(parameters.getString("fromOccurredAt")),
                parameters.getLong("chunkSize").intValue(),
                step == null ? 0 : step.getReadCount(),
                step == null ? 0 : step.getFilterCount(),
                step == null ? 0 : step.getWriteCount(),
                step == null ? 0 : step.getCommitCount(),
                start == null ? 0 : Duration.between(start, end).toMillis()
        );
    }
}
