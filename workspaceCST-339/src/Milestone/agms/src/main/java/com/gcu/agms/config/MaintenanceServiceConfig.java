package com.gcu.agms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gcu.agms.repository.MaintenanceRecordRepository;
import com.gcu.agms.service.impl.JdbcMaintenanceRecordService;
import com.gcu.agms.service.maintenance.MaintenanceRecordService;

/**
 * Configuration class for maintenance record service.
 * Defines beans for maintenance record service implementation.
 */
@Configuration
public class MaintenanceServiceConfig {

    /**
     * Creates a JDBC maintenance record service bean.
     * 
     * @param maintenanceRecordRepository Repository for maintenance record data access
     * @return a JdbcMaintenanceRecordService instance
     */
    @Bean
    public MaintenanceRecordService maintenanceRecordService(MaintenanceRecordRepository maintenanceRecordRepository) {
        return new JdbcMaintenanceRecordService(maintenanceRecordRepository);
    }
}