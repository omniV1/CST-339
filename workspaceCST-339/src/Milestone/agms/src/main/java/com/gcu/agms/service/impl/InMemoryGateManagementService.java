package com.gcu.agms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.gate.GateManagementService;

import jakarta.annotation.PostConstruct;

/**
 * An in-memory implementation of the GateManagementService interface that manages airport gates.
 * This service provides CRUD operations for gate management and stores gate data in memory using a HashMap.
 * 
 * The service includes functionality to:
 * - Create new gates
 * - Retrieve gates by ID or terminal
 * - Update existing gates
 * - Delete gates
 * - List all gates
 * 
 * The service also initializes with sample gate data for testing purposes,
 * creating 20 gates across 4 terminals (5 gates per terminal).
 * 
 * Gates are identified by a unique gateId in the format "T[terminal_number]G[gate_number]"
 * (e.g., "T1G1" for Terminal 1, Gate 1).
 * 
 * This implementation is suitable for testing and development purposes but not for production use
 * as data is not persisted between application restarts.
 * 
 * @Service indicates that this class is a Spring service component
 */
@Service
public class InMemoryGateManagementService implements GateManagementService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryGateManagementService.class);
    private final Map<String, GateModel> gates = new HashMap<>();
    
    @Override
    public boolean createGate(GateModel gate) {
        logger.info("Creating new gate: {}", gate.getGateId());
        
        if (gates.containsKey(gate.getGateId())) {
            logger.warn("Gate {} already exists", gate.getGateId());
            return false;
        }
        
        gates.put(gate.getGateId(), gate);
        logger.info("Gate {} created successfully", gate.getGateId());
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
                   .toList();
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