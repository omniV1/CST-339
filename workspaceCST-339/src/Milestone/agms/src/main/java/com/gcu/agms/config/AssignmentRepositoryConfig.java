package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.AssignmentRepository;
import com.gcu.agms.repository.impl.JdbcAssignmentRepository;

/**
 * Configuration class for assignment repository.
 * Defines beans for assignment repository implementation.
 */
@Configuration
public class AssignmentRepositoryConfig {

    /**
     * Creates a JDBC assignment repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcAssignmentRepository instance
     */
    @Bean
    public AssignmentRepository assignmentRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcAssignmentRepository(jdbcTemplate);
    }
}