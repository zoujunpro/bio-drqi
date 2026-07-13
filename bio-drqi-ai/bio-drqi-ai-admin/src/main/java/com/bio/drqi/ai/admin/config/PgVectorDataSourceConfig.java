package com.bio.drqi.ai.admin.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * pgvector 独立数据源。
 */
@Configuration
public class PgVectorDataSourceConfig {

    @Bean(name = "pgVectorDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pgvector")
    public DataSource pgVectorDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate(@Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
