package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.UserRepository;
import com.gcu.agms.repository.impl.JdbcUserRepository;

/**
 * Configuration class for user repository.
 * Defines beans for user repository implementation.
 */
@Configuration
public class UserServiceConfig {

    /**
     * Creates a JDBC user repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcUserRepository instance
     */
    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcUserRepository(jdbcTemplate);
    }
}