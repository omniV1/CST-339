package com.gcu.agms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.FlightModel;
import com.gcu.agms.model.GateModel;

/**
 * Implementation of FlightOperationsService that manages flight operations in memory.
 * This service demonstrates Spring's dependency injection by showing how multiple
 * services can work together while maintaining loose coupling.
 */
@Service
public class InMemoryFlightOperationsService implements FlightOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFlightOperationsService.class);
    
    // Injecting required services through constructor injection
    private final GateManagementService gateManagementService;
    private final GateOperationsService gateOperationsService;
    
    public InMemoryFlightOperationsService(
            GateManagementService gateManagementService,
            GateOperationsService gateOperationsService) {
        this.gateManagementService = gateManagementService;
        this.gateOperationsService = gateOperationsService;
    }
    
    @Override
    public Map<String, Integer> getOperationalStatistics() {
        logger.info("Generating operational statistics");
        Map<String, Integer> stats = new HashMap<>();
        
        // Combine information from multiple services to create comprehensive statistics
        var gateStats = gateOperationsService.getStatistics();
        var gates = gateManagementService.getAllGates();
        
        stats.put("availableGates", gateStats.get("availableGates"));
        stats.put("occupiedGates", gateStats.get("occupiedGates"));
        stats.put("maintenanceGates", gateStats.get("maintenanceGates"));
        stats.put("totalGates", gates.size());
        
        return stats;
    }
    
    @Override
    public boolean requestGateChange(String flightNumber, String currentGate, 
                                   String requestedGate, String reason) {
        logger.info("Processing gate change request for flight: {} from {} to {}", 
                   flightNumber, currentGate, requestedGate);
                   
        // Verify both gates exist
        Optional<GateModel> currentGateModel = gateManagementService.getGateById(currentGate);
        Optional<GateModel> requestedGateModel = gateManagementService.getGateById(requestedGate);
        
        if (currentGateModel.isEmpty() || requestedGateModel.isEmpty()) {
            logger.warn("Gate change request failed - invalid gate specified");
            return false;
        }
        
        // In a real implementation, this would create a change request record
        // For now, we'll just log it
        logger.info("Gate change request submitted successfully");
        return true;
    }
    
    @Override
    public List<FlightModel> getCurrentSchedule() {
        logger.info("Retrieving current flight schedule");
        // In a real implementation, this would fetch from a database
        // For now, return sample data
        return new ArrayList<>();
    }
    
    @Override
    public boolean createNewGate(String terminalNumber, String gateNumber) {
        logger.info("Creating new gate: T{}G{}", terminalNumber, gateNumber);
        
        GateModel newGate = new GateModel();
        newGate.setGateId(String.format("T%sG%s", terminalNumber, gateNumber));
        newGate.setTerminal(terminalNumber);
        newGate.setGateNumber(gateNumber);
        
        return gateManagementService.createGate(newGate);
    }
    
    @Override
public Map<String, Object> getGateUtilization() {
    logger.info("Retrieving gate utilization data");
    Map<String, Object> utilization = new HashMap<>();
    
    List<GateModel> gates = gateManagementService.getAllGates();
    Map<String, GateOperationsService.GateStatus> statuses = gateOperationsService.getAllGateStatuses();
    
    List<Map<String, Object>> gateStatusList = gates.stream()
        .map(gate -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("gate", gate.getGateId());
            entry.put("status", statuses.get(gate.getGateId()));
            entry.put("currentFlight", "-");
            entry.put("nextFlight", "-");
            return entry;
        })
        .collect(Collectors.toList());
    
    utilization.put("gateStatuses", gateStatusList);
    return utilization;
}
    
}