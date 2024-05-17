package com.cmsujeevan.cdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Primary
    @Bean(name = "datasource")
    @ConfigurationProperties(prefix = "spring.cdp-database")
    public DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "namedJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(getDataSource());
    }


}
