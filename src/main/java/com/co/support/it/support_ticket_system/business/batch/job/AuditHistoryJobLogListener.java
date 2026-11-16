package com.co.support.it.support_ticket_system.business.batch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.infrastructure.item.Chunk;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Escribe el avance del job en el logger {@value #LOGGER_NAME}, que logback-spring.xml envía
 * a logs/batch/audit-history.log: inicio, cada bloque, fin del paso y resultado.
 */
public class AuditHistoryJobLogListener
        implements JobExecutionListener, StepExecutionListener, ChunkListener<AuditEventRecord, AuditHistoryRecord>,
        ItemReadListener<AuditEventRecord>, ItemProcessListener<AuditEventRecord, AuditHistoryRecord> {

    public static final String LOGGER_NAME = "batch.audit-history";
    private static final Logger LOG = LoggerFactory.getLogger(LOGGER_NAME);

    private StepExecution stepExecution;
    private int chunkNumber;

    // Contadores propios: los de StepExecution se actualizan después de que termina cada bloque.
    private long totalRead;
    private long totalAlreadyCopied;
    private long totalWritten;
    private long chunkRead;
    private long chunkAlreadyCopied;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        var parameters = jobExecution.getJobParameters();
        LOG.info("[ejecución {}] Inicio del volcado | solicitado por: {} | desde: {} | tamaño de bloque: {}",
                jobExecution.getId(),
                parameters.getString("requestedBy"),
                parameters.getString("fromOccurredAt"),
                parameters.getLong("chunkSize"));
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.chunkNumber = 0;
        this.totalRead = 0;
        this.totalAlreadyCopied = 0;
        this.totalWritten = 0;
        LOG.info("[ejecución {}] Paso '{}' iniciado", stepExecution.getJobExecutionId(), stepExecution.getStepName());
    }

    @Override
    public void afterRead(AuditEventRecord item) {
        chunkRead++;
    }

    @Override
    public void afterProcess(AuditEventRecord item, AuditHistoryRecord result) {
        if (result == null) {
            chunkAlreadyCopied++;
        }
    }

    @Override
    public void afterChunk(Chunk<AuditHistoryRecord> chunk) {
        chunkNumber++;
        totalRead += chunkRead;
        totalAlreadyCopied += chunkAlreadyCopied;
        totalWritten += chunk.size();

        LOG.info("[ejecución {}] Bloque {}: leídos {}, ya copiados {}, escritos {} | acumulado: leídos {}, ya copiados {}, escritos {}",
                stepExecution.getJobExecutionId(), chunkNumber,
                chunkRead, chunkAlreadyCopied, chunk.size(),
                totalRead, totalAlreadyCopied, totalWritten);

        chunkRead = 0;
        chunkAlreadyCopied = 0;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOG.info("[ejecución {}] Paso '{}' terminado | estado: {} | leídos: {} | ya copiados: {} | escritos: {} | bloques: {}",
                stepExecution.getJobExecutionId(),
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getFilterCount(),
                stepExecution.getWriteCount(),
                stepExecution.getCommitCount());
        return stepExecution.getExitStatus();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime() != null ? jobExecution.getEndTime() : LocalDateTime.now();
        long millis = start == null ? 0 : Duration.between(start, end).toMillis();

        if (jobExecution.getAllFailureExceptions().isEmpty()) {
            LOG.info("[ejecución {}] Fin del volcado | estado: {} | duración: {} ms",
                    jobExecution.getId(), jobExecution.getStatus(), millis);
        } else {
            jobExecution.getAllFailureExceptions().forEach(error ->
                    LOG.error("[ejecución {}] Fin del volcado con errores | estado: {} | duración: {} ms",
                            jobExecution.getId(), jobExecution.getStatus(), millis, error));
        }
    }
}
