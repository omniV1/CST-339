package com.gcu.agms.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.service.gate.GateOperationsService;

/**
 * JDBC implementation of the GateOperationsService interface.
 * This service uses a GateRepository to access gate status data from a database.
 */
@Service("jdbcGateOperationsService")
@Primary
public class JdbcGateOperationsService implements GateOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(JdbcGateOperationsService.class);
    
    private final GateRepository gateRepository;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param gateRepository Repository for gate data access
     */
    public JdbcGateOperationsService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
        logger.info("Initialized JDBC Gate Operations Service");
    }

    @Override
    public Map<String, GateOperationsService.GateStatus> getAllGateStatuses() {
        logger.debug("Retrieving status for all gates");
        
        Map<String, GateOperationsService.GateStatus> statuses = new HashMap<>();
        
        // Retrieve all gates from database
        gateRepository.findAll().forEach(gate -> {
            // Convert database status (com.gcu.agms.model.gate.GateStatus) to 
            // service status (com.gcu.agms.service.gate.GateOperationsService.GateStatus)
            GateOperationsService.GateStatus status;
            String statusName = gate.getStatus().name();
            
            try {
                status = GateOperationsService.GateStatus.valueOf(statusName);
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown status for gate {}: {}", gate.getGateId(), statusName);
                status = GateOperationsService.GateStatus.AVAILABLE; // Default to AVAILABLE
            }
            
            statuses.put(gate.getGateId(), status);
        });
        
        return statuses;
    }

    @Override
    public Map<String, Integer> getStatistics() {
        logger.debug("Calculating gate statistics");
        
        Map<String, Integer> stats = new HashMap<>();
        
        // Count total gates
        stats.put("totalGates", gateRepository.countAll());
        
        // Count gates by status using the database model status values
        for (GateStatus status : GateStatus.values()) {
            int count = gateRepository.countByStatus(status.toString());
            stats.put(status.name().toLowerCase() + "Gates", count);
        }
        
        logger.info("Gate statistics calculated: {}", stats);
        
        return stats;
    }
}