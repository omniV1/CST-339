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

import com.gcu.agms.model.GateModel;

import jakarta.annotation.PostConstruct;

/**
 * Provides an in-memory implementation of gate management operations.
 * This implementation stores gate data in memory, making it suitable for
 * development and testing purposes. In a production environment, this would
 * typically be replaced with a database-backed implementation.
 */
@Service
public class InMemoryGateManagementService implements GateManagementService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryGateManagementService.class);
    private final Map<String, GateModel> gates = new HashMap<>();
    
    @Override
    public boolean createGate(GateModel gate) {
        logger.info("Attempting to create new gate with ID: {}", gate.getGateId());
        
        if (gates.containsKey(gate.getGateId())) {
            logger.warn("Gate creation failed: Gate ID {} already exists", gate.getGateId());
            return false;
        }
        
        gates.put(gate.getGateId(), gate);
        logger.info("Gate created successfully: {}", gate.getGateId());
        return true;
    }
    
    @Override
    public Optional<GateModel> getGateById(String gateId) {
        logger.debug("Retrieving gate with ID: {}", gateId);
        return Optional.ofNullable(gates.get(gateId));
    }
    
    @Override
    public List<GateModel> getAllGates() {
        logger.debug("Retrieving all gates");
        return new ArrayList<>(gates.values());
    }
    
    @Override
    public boolean updateGate(String gateId, GateModel gate) {
        logger.info("Attempting to update gate: {}", gateId);
        
        if (!gates.containsKey(gateId)) {
            logger.warn("Gate update failed: Gate ID {} not found", gateId);
            return false;
        }
        
        gates.put(gateId, gate);
        logger.info("Gate updated successfully: {}", gateId);
        return true;
    }
    
    @Override
    public boolean deleteGate(String gateId) {
        logger.info("Attempting to delete gate: {}", gateId);
        
        if (gates.remove(gateId) == null) {
            logger.warn("Gate deletion failed: Gate ID {} not found", gateId);
            return false;
        }
        
        logger.info("Gate deleted successfully: {}", gateId);
        return true;
    }
    
    @Override
    public List<GateModel> getGatesByTerminal(String terminal) {
        logger.debug("Retrieving gates for terminal: {}", terminal);
        return gates.values().stream()
                   .filter(gate -> gate.getTerminal().equals(terminal))
                   .collect(Collectors.toList());
    }
    @PostConstruct
public void initialize() {
    logger.info("Initializing sample gates");
    
    for (int terminal = 1; terminal <= 4; terminal++) {
        for (int gate = 1; gate <= 5; gate++) {
            GateModel gateModel = new GateModel();
            String gateId = String.format("T%dG%d", terminal, gate);
            gateModel.setGateId(gateId);
            gateModel.setTerminal(String.valueOf(terminal));
            gateModel.setGateNumber(String.valueOf(gate));
            gates.put(gateId, gateModel);
        }
    }
    
    logger.info("Initialized {} sample gates", gates.size());
}
}