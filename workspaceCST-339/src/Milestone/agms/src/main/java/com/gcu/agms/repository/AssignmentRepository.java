package com.gcu.agms.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;

/**
 * Repository interface for assignment data access operations.
 * Defines methods for performing CRUD operations and specific queries related to gate assignments.
 */
public interface AssignmentRepository {
    
    /**
     * Retrieve all assignments.
     * 
     * @return List of all assignments
     */
    List<AssignmentModel> findAll();
    
    /**
     * Find an assignment by its ID.
     * 
     * @param id The database ID to search for
     * @return Optional containing the assignment if found, empty Optional otherwise
     */
    Optional<AssignmentModel> findById(Long id);
    
    /**
     * Find assignments for a specific gate.
     * 
     * @param gateId The gate ID to search for
     * @return List of assignments for the specified gate
     */
    List<AssignmentModel> findByGateId(String gateId);
    
    /**
     * Find assignments for a specific flight.
     * 
     * @param flightNumber The flight number to search for
     * @return List of assignments for the specified flight
     */
    List<AssignmentModel> findByFlightNumber(String flightNumber);
    
    /**
     * Find assignments by status.
     * 
     * @param status The status to search for
     * @return List of assignments with the specified status
     */
    List<AssignmentModel> findByStatus(AssignmentStatus status);
    
    /**
     * Find active assignments (current time between start and end time).
     * 
     * @return List of active assignments
     */
    List<AssignmentModel> findActiveAssignments();
    
    /**
     * Find upcoming assignments (start time in the future).
     * 
     * @return List of upcoming assignments
     */
    List<AssignmentModel> findUpcomingAssignments();
    
    /**
     * Get current and next assignments for a gate.
     * 
     * @param gateId The gate ID to get assignments for
     * @return Map containing "current" and "next" assignments (if they exist)
     */
    Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId);
    
    /**
     * Save an assignment to the database.
     * If the assignment has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param assignment The assignment to save
     * @return The saved assignment with generated ID (for new records)
     */
    AssignmentModel save(AssignmentModel assignment);
    
    /**
     * Delete an assignment by its ID.
     * 
     * @param id The ID of the assignment to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(Long id);
    
    /**
     * Update the status of an assignment.
     * 
     * @param id The ID of the assignment to update
     * @param status The new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(Long id, AssignmentStatus status);
    
    /**
     * Mark an assignment as cancelled.
     * 
     * @param id The ID of the assignment to cancel
     * @return true if cancellation was successful, false otherwise
     */
    boolean cancelAssignment(Long id);
    
    /**
     * Check if an assignment has a time conflict with existing assignments.
     * 
     * @param assignment The assignment to check for conflicts
     * @return true if there is a conflict, false otherwise
     */
    boolean hasConflict(AssignmentModel assignment);
    
    /**
     * Count assignments by status.
     * 
     * @param status The status to count
     * @return The number of assignments with the specified status
     */
    int countByStatus(AssignmentStatus status);
    
    /**
     * Count assignments by gate.
     * 
     * @param gateId The gate ID to count
     * @return The number of assignments for the specified gate
     */
    int countByGateId(String gateId);
    
    /**
     * Count all assignments.
     * 
     * @return The total number of assignments
     */
    int countAll();
}