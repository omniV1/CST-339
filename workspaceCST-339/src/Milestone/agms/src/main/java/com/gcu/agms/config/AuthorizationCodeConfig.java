package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.AuthorizationCodeRepository;
import com.gcu.agms.repository.impl.JdbcAuthorizationCodeRepository;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.impl.DatabaseAuthorizationCodeService;

/**
 * Configuration class for authorization code components.
 * Explicitly defines the beans needed for authorization code functionality.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
@Configuration
public class AuthorizationCodeConfig {

    /**
     * Creates the authorization code repository bean.
     * 
     * @param jdbcTemplate JDBC template for database operations
     * @return The authorization code repository
     */
    @Bean
    AuthorizationCodeRepository authorizationCodeRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcAuthorizationCodeRepository(jdbcTemplate);
    }

    /**
     * Creates the authorization code service bean.
     * 
     * @param repository Repository for authorization code data access
     * @return The authorization code service
     */
    @Bean
    AuthorizationCodeService authorizationCodeService(AuthorizationCodeRepository repository) {
        return new DatabaseAuthorizationCodeService(repository);
    }
}