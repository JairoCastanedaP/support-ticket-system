package com.co.support.it.support_ticket_system.infrastructure.persistence.oracle;

import com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.entity.AuditEventEntity;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
        basePackages = "com.co.support.it.support_ticket_system.infrastructure.persistence.oracle.repository",
        entityManagerFactoryRef = "oracleAuditEntityManagerFactory",
        transactionManagerRef = "oracleAuditTransactionManager"
)
public class OracleAuditPersistenceConfiguration {

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("app.datasource.oracle")
    public DataSourceProperties oracleAuditDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(defaultCandidate = false)
    @ConfigurationProperties("app.datasource.oracle.configuration")
    public HikariDataSource oracleAuditDataSource(
            @Qualifier("oracleAuditDataSourceProperties")
            DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(defaultCandidate = false)
    public LocalContainerEntityManagerFactoryBean oracleAuditEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("oracleAuditDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(AuditEventEntity.class)
                .persistenceUnit("oracleAudit")
                .properties(Map.of("hibernate.hbm2ddl.auto", "update"))
                .build();
    }

    @Bean(defaultCandidate = false)
    public PlatformTransactionManager oracleAuditTransactionManager(
            @Qualifier("oracleAuditEntityManagerFactory")
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}