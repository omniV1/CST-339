package com.gcu.agms.service.gate;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.gate.GateModel;

/**
 * Defines the contract for managing airport gates.
 * This service handles the creation, retrieval, updating, and deletion of gates,
 * serving as the primary interface for gate management operations.
 */
public interface GateManagementService {
    /**
     * Creates a new gate in the system.
     * @param gate The gate details to be created
     * @return true if creation was successful, false if a gate with the same ID already exists
     */
    boolean createGate(GateModel gate);
    
    /**
     * Retrieves a specific gate by its ID.
     * @param gateId The unique identifier of the gate
     * @return Optional containing the gate if found, empty Optional otherwise
     */
    Optional<GateModel> getGateById(String gateId);
    
    /**
     * Retrieves all gates in the system.
     * @return List of all gates
     */
    List<GateModel> getAllGates();
    
    /**
     * Updates an existing gate's information.
     * @param gateId The ID of the gate to update
     * @param gate The updated gate information
     * @return true if update was successful, false if gate doesn't exist
     */
    boolean updateGate(String gateId, GateModel gate);
    
    /**
     * Removes a gate from the system.
     * @param gateId The ID of the gate to remove
     * @return true if deletion was successful, false if gate doesn't exist
     */
    boolean deleteGate(String gateId);
    
    /**
     * Retrieves all gates in a specific terminal.
     * @param terminal The terminal number
     * @return List of gates in the specified terminal
     */
    List<GateModel> getGatesByTerminal(String terminal);
}