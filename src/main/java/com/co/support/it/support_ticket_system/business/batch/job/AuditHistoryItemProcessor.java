package com.co.support.it.support_ticket_system.business.batch.job;

import com.co.support.it.support_ticket_system.business.batch.repository.AuditHistoryRepository;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import tools.jackson.databind.json.JsonMapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

/**
 * Procesador del job: descarta los eventos que ya están en el histórico (cuentan como filtrados)
 * y transforma los nuevos con {@link AuditEventClassifier}.
 */
public class AuditHistoryItemProcessor
        implements ItemProcessor<AuditEventRecord, AuditHistoryRecord>, StepExecutionListener {

    private final AuditHistoryRepository historyRepository;
    private final JsonMapper jsonMapper;
    private final OffsetDateTime fromOccurredAt;

    private Set<String> alreadyCopied = Set.of();
    private Long jobExecutionId;
    private OffsetDateTime exportedAt;

    public AuditHistoryItemProcessor(AuditHistoryRepository historyRepository, JsonMapper jsonMapper,
                                     OffsetDateTime fromOccurredAt) {
        this.historyRepository = historyRepository;
        this.jsonMapper = jsonMapper;
        this.fromOccurredAt = fromOccurredAt;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        alreadyCopied = historyRepository.findEventIdsSince(fromOccurredAt);
        jobExecutionId = stepExecution.getJobExecutionId();
        exportedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public AuditHistoryRecord process(AuditEventRecord event) {
        if (alreadyCopied.contains(event.eventId())) {
            return null;
        }
        return AuditEventClassifier.classify(event, jobExecutionId, jsonMapper, exportedAt);
    }
}
