package com.co.support.it.support_ticket_system.infrastructure.persistence.history;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.batch.autoconfigure.BatchTransactionManager;
import org.springframework.boot.batch.jdbc.autoconfigure.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Tercera base: histórico de auditoría (PostgreSQL; H2 en el perfil local).
 * También guarda las tablas de metadatos de Spring Batch ({@link BatchDataSource}),
 * así cada ejecución del job, sus pasos y sus parámetros quedan registrados junto al histórico.
 * Ningún bean es candidato por defecto, para no reemplazar la base operativa (MySQL).
 */
@Configuration(proxyBeanMethods = false)
public class HistoryPersistenceConfiguration {

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("app.datasource.history")
    public DataSourceProperties historyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(defaultCandidate = false)
    @BatchDataSource
    @ConfigurationProperties("app.datasource.history.configuration")
    public HikariDataSource historyDataSource(
            @Qualifier("historyDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(defaultCandidate = false)
    @BatchTransactionManager
    public PlatformTransactionManager historyTransactionManager(@Qualifier("historyDataSource") DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    /**
     * Crea las tablas propias del histórico (db/history/schema.sql). Se puede repetir sin error.
     */
    @Bean
    public DataSourceInitializer historyDataSourceInitializer(@Qualifier("historyDataSource") DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/history/schema.sql")));
        return initializer;
    }
}
