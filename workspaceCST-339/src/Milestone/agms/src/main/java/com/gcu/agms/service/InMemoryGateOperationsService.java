package com.gcu.agms.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Provides an in-memory implementation of gate operations management.
 * This implementation maintains gate statuses in memory and provides statistical data
 * about gate usage. It's suitable for development and testing, simulating a real
 * airport's gate management system.
 */
@Service
public class InMemoryGateOperationsService implements GateOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryGateOperationsService.class);
    private final Map<String, GateStatus> gateStatuses = new HashMap<>();

    @PostConstruct
    public void initializeData() {
        logger.info("Initializing in-memory gate data");
        
        // Create sample gates for each terminal
        for (int terminal = 1; terminal <= 4; terminal++) {
            for (int gate = 1; gate <= 5; gate++) {
                String gateId = String.format("T%dG%d", terminal, gate);
                // Randomly assign an initial status to each gate
                int random = new Random().nextInt(3);
                GateStatus status = GateStatus.values()[random];
                gateStatuses.put(gateId, status);
                logger.debug("Initialized gate {} with status {}", gateId, status);
            }
        }
        
        logger.info("Gate initialization complete. Total gates: {}", gateStatuses.size());
    }

    @Override
    public Map<String, GateStatus> getAllGateStatuses() {
        logger.debug("Retrieving status for all gates");
        // Return a new HashMap to prevent external modification of internal state
        return new HashMap<>(gateStatuses);
    }

    @Override
    public Map<String, Integer> getStatistics() {
        logger.debug("Calculating gate statistics");
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", gateStatuses.size());
        stats.put("availableGates", countGatesByStatus(GateStatus.AVAILABLE));
        stats.put("occupiedGates", countGatesByStatus(GateStatus.OCCUPIED));
        stats.put("maintenanceGates", countGatesByStatus(GateStatus.MAINTENANCE));
        
        logger.info("Gate statistics calculated - Total: {}, Available: {}, Occupied: {}, Maintenance: {}",
                   stats.get("totalGates"),
                   stats.get("availableGates"),
                   stats.get("occupiedGates"),
                   stats.get("maintenanceGates"));
        
        return stats;
    }

    /**
     * Helper method to count gates by their status.
     * @param status The status to count
     * @return The number of gates with the specified status
     */
    private int countGatesByStatus(GateStatus status) {
        return (int) gateStatuses.values()
                .stream()
                .filter(s -> s == status)
                .count();
    }
}