package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gcu.agms.repository.AssignmentRepository;
import com.gcu.agms.repository.AuthorizationCodeRepository;
import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.repository.MaintenanceRecordRepository;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.flight.AssignmentService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;
import com.gcu.agms.service.impl.DatabaseAuthorizationCodeService;
import com.gcu.agms.service.impl.JdbcAssignmentService;
import com.gcu.agms.service.impl.JdbcGateManagementService;
import com.gcu.agms.service.impl.JdbcGateOperationsService;
import com.gcu.agms.service.impl.JdbcMaintenanceRecordService;
import com.gcu.agms.service.maintenance.MaintenanceRecordService;

/**
 * Unified configuration class for all services.
 * This class consolidates all service bean definitions in one place,
 * eliminating the need for separate configuration classes for each service type.
 */
@Configuration
public class ServiceConfig {

    /**
     * Creates an authorization code service bean.
     * 
     * @param repository Repository for authorization code data access
     * @return The authorization code service
     */
    @Bean
    AuthorizationCodeService authorizationCodeService(AuthorizationCodeRepository repository) {
        return new DatabaseAuthorizationCodeService(repository);
    }
    
    /**
     * Creates a maintenance record service bean.
     * 
     * @param maintenanceRecordRepository Repository for maintenance record data access
     * @return a JdbcMaintenanceRecordService instance
     */
    @Bean
    public MaintenanceRecordService maintenanceRecordService(MaintenanceRecordRepository maintenanceRecordRepository) {
        return new JdbcMaintenanceRecordService(maintenanceRecordRepository);
    }
    
    /**
     * Creates an assignment service bean.
     * 
     * @param assignmentRepository Repository for assignment data access
     * @return a JdbcAssignmentService instance
     */
    @Bean
    public AssignmentService assignmentService(AssignmentRepository assignmentRepository) {
        return new JdbcAssignmentService(assignmentRepository);
    }
    
    /**
     * Creates a gate operations service bean.
     * 
     * @param gateRepository Repository for gate data access
     * @return a JdbcGateOperationsService instance
     */
    @Bean
    public GateOperationsService gateOperationsService(GateRepository gateRepository) {
        return new JdbcGateOperationsService(gateRepository);
    }
    
    /**
     * Creates a gate management service bean.
     * 
     * @param gateRepository Repository for gate data access
     * @return a JdbcGateManagementService instance
     */
    @Bean
    public GateManagementService gateManagementService(GateRepository gateRepository) {
        return new JdbcGateManagementService(gateRepository);
    }

 
}