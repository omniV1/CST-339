package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.MaintenanceRecordRepository;
import com.gcu.agms.repository.impl.JdbcMaintenanceRecordRepository;

/**
 * Configuration class for maintenance record repository.
 * Defines beans for maintenance record repository implementation.
 */
@Configuration
public class MaintenanceRecordRepositoryConfig {

    /**
     * Creates a JDBC maintenance record repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcMaintenanceRecordRepository instance
     */
    @Bean
    public MaintenanceRecordRepository maintenanceRecordRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcMaintenanceRecordRepository(jdbcTemplate);
    }
}