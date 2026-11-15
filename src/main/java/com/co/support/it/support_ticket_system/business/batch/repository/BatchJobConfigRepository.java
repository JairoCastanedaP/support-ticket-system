package com.co.support.it.support_ticket_system.business.batch.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Lee la parametrización de los jobs (tabla batch_job_config de la base histórica).
 */
@Repository
public class BatchJobConfigRepository {

    private final JdbcTemplate jdbc;

    public BatchJobConfigRepository(@Qualifier("historyDataSource") DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public Optional<Integer> findChunkSize(String jobName) {
        List<Integer> values = jdbc.queryForList(
                "SELECT chunk_size FROM batch_job_config WHERE job_name = ?", Integer.class, jobName);
        return values.stream().findFirst();
    }
}
