package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gcu.agms.repository.AircraftRepository;
import com.gcu.agms.repository.AssignmentRepository;
import com.gcu.agms.repository.AuthorizationCodeRepository;
import com.gcu.agms.repository.FlightRepository;
import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.repository.MaintenanceRecordRepository;
import com.gcu.agms.repository.UserRepository;
import com.gcu.agms.repository.impl.JdbcAircraftRepository;
import com.gcu.agms.repository.impl.JdbcAssignmentRepository;
import com.gcu.agms.repository.impl.JdbcAuthorizationCodeRepository;
import com.gcu.agms.repository.impl.JdbcFlightRepository;
import com.gcu.agms.repository.impl.JdbcGateRepository;
import com.gcu.agms.repository.impl.JdbcMaintenanceRecordRepository;
import com.gcu.agms.repository.impl.JdbcUserRepository;

/**
 * Unified configuration class for all repositories.
 * This class consolidates all repository bean definitions in one place,
 * eliminating the need for separate configuration classes for each repository type.
 */
@Configuration
public class RepositoryConfig {

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
    
    /**
     * Creates a JDBC authorization code repository bean.
     * 
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @return a JdbcAuthorizationCodeRepository instance
     */
    @Bean
    public AuthorizationCodeRepository authorizationCodeRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcAuthorizationCodeRepository(jdbcTemplate);
    }

}