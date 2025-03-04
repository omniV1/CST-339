package com.gcu.agms.service.flight;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.gcu.agms.model.gate.AssignmentModel;

/**
 * Service interface for gate assignment operations.
 * Defines methods for managing gate assignments.
 */
public interface AssignmentService {
    
    /**
     * Creates a new gate assignment after checking for conflicts.
     * 
     * @param assignment The assignment to create
     * @return true if creation was successful, false if there was a conflict
     */
    boolean createAssignment(AssignmentModel assignment);
    
    /**
     * Retrieves all assignments for a specific gate.
     * 
     * @param gateId The gate ID to get assignments for
     * @return List of assignments for the gate
     */
    List<AssignmentModel> getAssignmentsForGate(String gateId);
    
    /**
     * Gets the current and next assignments for a gate.
     * 
     * @param gateId The gate ID to get assignments for
     * @return Map containing "current" and "next" assignments if they exist
     */
    Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId);
    
    /**
     * Updates an existing assignment.
     * 
     * @param gateId The ID of the gate containing the assignment
     * @param assignmentId The ID of the assignment to update
     * @param updated The updated assignment data
     * @return true if successfully updated, false if not found or conflict exists
     */
    boolean updateAssignment(String gateId, Long assignmentId, AssignmentModel updated);
    
    /**
     * Deletes an assignment from a specific gate.
     * 
     * @param gateId The ID of the gate containing the assignment
     * @param assignmentId The ID of the assignment to delete
     * @return true if successfully deleted, false if not found
     */
    boolean deleteAssignment(String gateId, Long assignmentId);
    
    /**
     * Updates a specific field of an assignment.
     * 
     * @param gateId The ID of the gate containing the assignment
     * @param assignmentId The ID of the assignment to update
     * @param updater A consumer function that updates the assignment
     * @return true if successfully updated, false if not found
     */
    boolean updateAssignmentField(String gateId, Long assignmentId, Consumer<AssignmentModel> updater);
    
    /**
     * Gets all current assignments across all gates.
     * 
     * @return Map of gate IDs to their current assignments
     */
    Map<String, AssignmentModel> getCurrentAssignments();
    
    /**
     * Gets the current assignment for a specific gate.
     * 
     * @param gateId The gate ID to get current assignment for
     * @return Optional containing the current assignment if it exists
     */
    Optional<AssignmentModel> getCurrentAssignment(String gateId);
    
    /**
     * Checks if an assignment is currently active.
     * 
     * @param assignment The assignment to check
     * @return true if the assignment is current (active now), false otherwise
     */
    boolean isCurrentAssignment(AssignmentModel assignment);
    
    /**
     * Checks if an assignment has conflicts with existing assignments.
     * 
     * @param assignment The assignment to check
     * @return true if there are conflicts, false otherwise
     */
    boolean hasConflict(AssignmentModel assignment);
}