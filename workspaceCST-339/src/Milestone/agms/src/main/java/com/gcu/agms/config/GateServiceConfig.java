package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.repository.impl.JdbcGateRepository;

/**
 * Configuration class for gate repository.
 * Defines beans for gate repository implementation.
 */
@Configuration
public class GateServiceConfig {

    /**
     * Creates a JDBC gate repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcGateRepository instance
     */
    @Bean
    public GateRepository gateRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcGateRepository(jdbcTemplate);
    }
}