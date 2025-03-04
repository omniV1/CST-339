package com.gcu.agms.repository;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.gate.GateModel;

/**
 * Repository interface for gate data access operations.
 * Defines methods for performing CRUD operations and specific queries related to gates.
 */
public interface GateRepository {
    
    /**
     * Retrieve all gates.
     * 
     * @return List of all gates
     */
    List<GateModel> findAll();
    
    /**
     * Find a gate by its database ID.
     * 
     * @param id The database ID to search for
     * @return Optional containing the gate if found, empty Optional otherwise
     */
    Optional<GateModel> findById(Long id);
    
    /**
     * Find a gate by its gate ID (e.g., "T1G1").
     * 
     * @param gateId The gate ID to search for
     * @return Optional containing the gate if found, empty Optional otherwise
     */
    Optional<GateModel> findByGateId(String gateId);
    
    /**
     * Find gates by terminal.
     * 
     * @param terminal The terminal to search for
     * @return List of gates in the specified terminal
     */
    List<GateModel> findByTerminal(String terminal);
    
    /**
     * Find gates by status.
     * 
     * @param status The status to search for
     * @return List of gates with the specified status
     */
    List<GateModel> findByStatus(String status);
    
    /**
     * Save a gate to the database.
     * If the gate has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param gate The gate to save
     * @return The saved gate with generated ID (for new records)
     */
    GateModel save(GateModel gate);
    
    /**
     * Delete a gate by its database ID.
     * 
     * @param id The ID of the gate to delete
     */
    void deleteById(Long id);
    
    /**
     * Check if a gate exists with the given gate ID.
     * 
     * @param gateId The gate ID to check
     * @return true if a gate exists with the gate ID, false otherwise
     */
    boolean existsByGateId(String gateId);
    
    /**
     * Count gates by status.
     * 
     * @param status The status to count
     * @return The number of gates with the specified status
     */
    int countByStatus(String status);
    
    /**
     * Count all gates.
     * 
     * @return The total number of gates
     */
    int countAll();
}