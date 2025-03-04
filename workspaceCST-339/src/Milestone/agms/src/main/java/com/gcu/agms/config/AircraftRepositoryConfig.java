package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.AircraftRepository;
import com.gcu.agms.repository.impl.JdbcAircraftRepository;

/**
 * Configuration class for aircraft repository.
 * Defines beans for aircraft repository implementation.
 */
@Configuration
public class AircraftRepositoryConfig {

    /**
     * Creates a JDBC aircraft repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcAircraftRepository instance
     */
    @Bean
    public AircraftRepository aircraftRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcAircraftRepository(jdbcTemplate);
    }
}