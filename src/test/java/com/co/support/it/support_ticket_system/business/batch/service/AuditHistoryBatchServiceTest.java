package com.co.support.it.support_ticket_system.business.batch.service;

import com.co.support.it.support_ticket_system.business.batch.repository.AuditHistoryRepository;
import com.co.support.it.support_ticket_system.business.batch.repository.BatchJobConfigRepository;
import com.co.support.it.support_ticket_system.generic.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.data.domain.AuditorAware;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditHistoryBatchServiceTest {

    private JobOperator jobOperator;
    private AuditHistoryRepository historyRepository;
    private BatchJobConfigRepository configRepository;
    private AuditHistoryBatchService service;

    @BeforeEach
    void setUp() {
        jobOperator = mock(JobOperator.class);
        historyRepository = mock(AuditHistoryRepository.class);
        configRepository = mock(BatchJobConfigRepository.class);
        AuditorAware<String> auditor = () -> Optional.of("jcastaneda");

        service = new AuditHistoryBatchService(jobOperator, mock(Job.class), historyRepository, configRepository, auditor, 10);
    }

    @Test
    void parameters_emptyHistoryCopiesEverythingWithTheConfiguredChunk() {
        when(configRepository.findChunkSize("auditHistoryJob")).thenReturn(Optional.of(25));
        when(historyRepository.findLastOccurredAt()).thenReturn(null);

        JobParameters parameters = service.buildParameters();

        assertThat(parameters.getLong("chunkSize")).isEqualTo(25L);
        assertThat(parameters.getString("fromOccurredAt")).isEqualTo("1970-01-01T00:00Z");
        assertThat(parameters.getString("requestedBy")).isEqualTo("jcastaneda");
    }

    @Test
    void parameters_startTenMinutesBeforeTheLastCopiedEvent() {
        when(configRepository.findChunkSize("auditHistoryJob")).thenReturn(Optional.of(100));
        when(historyRepository.findLastOccurredAt()).thenReturn(Instant.parse("2026-11-16T14:30:00Z"));

        assertThat(service.buildParameters().getString("fromOccurredAt")).isEqualTo("2026-11-16T14:20Z");
    }

    @Test
    void parameters_withoutConfigurationUseTheDefaultChunk() {
        when(configRepository.findChunkSize("auditHistoryJob")).thenReturn(Optional.empty());

        assertThat(service.buildParameters().getLong("chunkSize")).isEqualTo(100L);
    }

    @Test
    void run_rejectsASecondExecutionWhileOneIsInProgress() throws Exception {
        when(configRepository.findChunkSize(any())).thenReturn(Optional.of(100));
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        when(jobOperator.start(any(Job.class), any(JobParameters.class))).thenAnswer(invocation -> {
            started.countDown();
            release.await(5, TimeUnit.SECONDS);
            throw new IllegalStateException("fin de la prueba");
        });

        Thread first = new Thread(() -> {
            try {
                service.run();
            } catch (RuntimeException ignored) {
                // La primera ejecución termina con el error simulado.
            }
        });
        first.start();
        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

        assertThatThrownBy(() -> service.run())
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("en curso");

        release.countDown();
        first.join(5000);
    }
}
