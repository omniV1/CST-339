package com.gcu.agms.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.service.gate.GateOperationsService;

@Service("jdbcGateOperationsService")
@Primary
public class JdbcGateOperationsService implements GateOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(JdbcGateOperationsService.class);
    
    private final GateRepository gateRepository;
    
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
            // Convert database status to service status with enhanced error handling
            GateOperationsService.GateStatus status;
            String statusName = gate.getStatus().name();
            
            try {
                // Try to convert directly
                status = GateOperationsService.GateStatus.valueOf(statusName);
            } catch (IllegalArgumentException e) {
                // If status doesn't exist in enum, map to an appropriate default
                logger.warn("Unknown status for gate {}: {}", gate.getGateId(), statusName);
                
                // Map unknown statuses to appropriate values
                status = switch(statusName) {
                    case "CLOSED" -> GateOperationsService.GateStatus.MAINTENANCE;
                    case "UNKNOWN" -> GateOperationsService.GateStatus.AVAILABLE;
                    default -> GateOperationsService.GateStatus.AVAILABLE;
                };
                
                logger.info("Mapped unknown status {} to {} for gate {}", 
                    statusName, status, gate.getGateId());
            }
            
            statuses.put(gate.getGateId(), status);
        });
        
        logger.debug("Retrieved {} gate statuses", statuses.size());
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