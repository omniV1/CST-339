package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.FlightRepository;
import com.gcu.agms.repository.impl.JdbcFlightRepository;

/**
 * Configuration class for flight repository.
 * Defines beans for flight repository implementation.
 */
@Configuration
public class FlightRepositoryConfig {

    /**
     * Creates a JDBC flight repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcFlightRepository instance
     */
    @Bean
    public FlightRepository flightRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcFlightRepository(jdbcTemplate);
    }
}