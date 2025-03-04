package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.repository.GateRepository;
import com.gcu.agms.service.gate.GateManagementService;

/**
 * JDBC implementation of the GateManagementService interface.
 * This service uses a GateRepository to access and manage gate data from a database.
 */
@Service("jdbcGateManagementService")
@Primary
public class JdbcGateManagementService implements GateManagementService {
    private static final Logger logger = LoggerFactory.getLogger(JdbcGateManagementService.class);
    
    private final GateRepository gateRepository;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param gateRepository Repository for gate data access
     */
    public JdbcGateManagementService(GateRepository gateRepository) {
        this.gateRepository = gateRepository;
        logger.info("Initialized JDBC Gate Management Service");
    }

    @Override
    public boolean createGate(GateModel gate) {
        logger.info("Creating new gate: {}", gate.getGateId());
        
        // Validate gate
        if (!validateGate(gate)) {
            logger.warn("Gate creation failed: Invalid gate data");
            return false;
        }
        
        // Check if gate ID already exists
        if (gateRepository.existsByGateId(gate.getGateId())) {
            logger.warn("Gate creation failed: Gate ID already exists");
            return false;
        }
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        gate.setCreatedAt(now);
        gate.setUpdatedAt(now);
        
        // Save gate
        gateRepository.save(gate);
        logger.info("Gate created successfully: {}", gate.getGateId());
        return true;
    }

    @Override
    public Optional<GateModel> getGateById(String gateId) {
        logger.debug("Retrieving gate with ID: {}", gateId);
        return gateRepository.findByGateId(gateId);
    }

    @Override
    public List<GateModel> getAllGates() {
        logger.debug("Retrieving all gates");
        return gateRepository.findAll();
    }

    @Override
    public boolean updateGate(String gateId, GateModel gate) {
        logger.info("Updating gate: {}", gateId);
        
        // Validate gate
        if (!validateGate(gate)) {
            logger.warn("Gate update failed: Invalid gate data");
            return false;
        }
        
        // Find existing gate
        Optional<GateModel> existingGate = gateRepository.findByGateId(gateId);
        if (existingGate.isEmpty()) {
            logger.warn("Gate update failed: Gate not found");
            return false;
        }
        
        // Update gate data
        GateModel updatedGate = existingGate.get();
        updateGateFields(updatedGate, gate);
        updatedGate.setUpdatedAt(LocalDateTime.now());
        
        // Save updated gate
        gateRepository.save(updatedGate);
        logger.info("Gate updated successfully: {}", gateId);
        return true;
    }

    @Override
    public boolean deleteGate(String gateId) {
        logger.info("Deleting gate: {}", gateId);
        
        // Find gate
        Optional<GateModel> gate = gateRepository.findByGateId(gateId);
        if (gate.isEmpty()) {
            logger.warn("Gate deletion failed: Gate not found");
            return false;
        }
        
        // Delete gate
        gateRepository.deleteById(gate.get().getId());
        logger.info("Gate deleted successfully: {}", gateId);
        return true;
    }

    @Override
    public List<GateModel> getGatesByTerminal(String terminal) {
        logger.debug("Retrieving gates for terminal: {}", terminal);
        return gateRepository.findByTerminal(terminal);
    }
    
    /**
     * Validates a gate model for creation and updates.
     * 
     * @param gate The gate model to validate
     * @return true if valid, false otherwise
     */
    private boolean validateGate(GateModel gate) {
        return gate != null && 
            gate.getGateId() != null && !gate.getGateId().trim().isEmpty() &&
            gate.getTerminal() != null && !gate.getTerminal().trim().isEmpty() &&
            gate.getGateNumber() != null && !gate.getGateNumber().trim().isEmpty() &&
            gate.getGateType() != null &&
            gate.getGateSize() != null &&
            gate.getStatus() != null;
    }
    
    /**
     * Updates the fields of an existing gate with values from a new gate.
     * 
     * @param existingGate The existing gate to update
     * @param newGate The new gate with updated values
     */
    private void updateGateFields(GateModel existingGate, GateModel newGate) {
        // Don't update gateId as it's the unique identifier
        existingGate.setTerminal(newGate.getTerminal());
        existingGate.setGateNumber(newGate.getGateNumber());
        existingGate.setGateType(newGate.getGateType());
        existingGate.setGateSize(newGate.getGateSize());
        existingGate.setStatus(newGate.getStatus());
        existingGate.setIsActive(newGate.getIsActive());
        existingGate.setHasJetBridge(newGate.isHasJetBridge());
        existingGate.setCapacity(newGate.getCapacity());
    }
}