package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;

import jakarta.annotation.PostConstruct;

/**
 * In-memory implementation of assignment management.
 * This service maintains gate assignments and their statuses in memory,
 * providing CRUD operations and status management functionality.
 */
@Service
public class InMemoryAssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryAssignmentService.class);
    private static final String SYSTEM_USER = "system";  // Added constant
    
    // Store assignments by gate ID for easy lookup
    private final Map<String, List<AssignmentModel>> assignmentsByGate = new HashMap<>();
    private Long nextId = 1L;  // Simple ID generator
    
    @PostConstruct
    public void initialize() {
        logger.info("Initializing sample assignments");
        createSampleAssignments();
    }
    
    /**
     * Creates sample assignments for testing purposes.
     * In a real application, this would be replaced with database data.
     */
    private void createSampleAssignments() {
        // Create some sample assignments for different gates
        createSampleAssignment("T1G1", "AA123", LocalDateTime.now().plusHours(1), 
                             LocalDateTime.now().plusHours(3), SYSTEM_USER);  // Use constant
        createSampleAssignment("T2G3", "UA456", LocalDateTime.now().plusHours(2), 
                             LocalDateTime.now().plusHours(4), SYSTEM_USER);  // Use constant
        createSampleAssignment("T3G2", "DL789", LocalDateTime.now().plusHours(3), 
                             LocalDateTime.now().plusHours(5), SYSTEM_USER);  // Use constant
                             
        logger.info("Sample assignments created");
    }
    
    
    /**
     * Helper method to create a sample assignment.
     */
    private void createSampleAssignment(String gateId, String flightNumber, 
                                      LocalDateTime start, LocalDateTime end, 
                                      String assignedBy) {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setId(nextId++);
        assignment.setGateId(gateId);
        assignment.setFlightNumber(flightNumber);
        assignment.setStartTime(start);
        assignment.setEndTime(end);
        assignment.setAssignedBy(assignedBy);
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        assignment.initializeTimestamps();
        
        assignmentsByGate.computeIfAbsent(gateId, k -> new ArrayList<>()).add(assignment);
    }
    
    /**
     * Gets all assignments for a specific gate.
     */
    public List<AssignmentModel> getAssignmentsForGate(String gateId) {
        return assignmentsByGate.getOrDefault(gateId, new ArrayList<>());
    }
    
    /**
     * Gets the current and next assignments for a gate.
     * Returns a map containing "current" and "next" assignments if they exist.
     */
    public Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId) {
        Map<String, AssignmentModel> result = new HashMap<>();
        List<AssignmentModel> gateAssignments = getAssignmentsForGate(gateId);
        LocalDateTime now = LocalDateTime.now();
        
        // Sort assignments by start time
        gateAssignments.sort((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
        
        // Find current assignment
        Optional<AssignmentModel> currentAssignment = gateAssignments.stream()
            .filter(a -> !a.isCancelled() && 
                        now.isAfter(a.getStartTime()) && 
                        now.isBefore(a.getEndTime()))
            .findFirst();
        
        // Find next assignment
        Optional<AssignmentModel> nextAssignment = gateAssignments.stream()
            .filter(a -> !a.isCancelled() && now.isBefore(a.getStartTime()))
            .findFirst();
        
        currentAssignment.ifPresent(a -> result.put("current", a));
        nextAssignment.ifPresent(a -> result.put("next", a));
        
        return result;
    }
    
    /**
     * Creates a new assignment after validating for conflicts.
     */
    public boolean createAssignment(AssignmentModel assignment) {
        List<AssignmentModel> existingAssignments = 
            assignmentsByGate.getOrDefault(assignment.getGateId(), new ArrayList<>());
        
        // Check for conflicts
        boolean hasConflict = existingAssignments.stream()
            .anyMatch(existing -> !existing.isCancelled() && 
                                existing.hasConflict(assignment));
        
        if (hasConflict) {
            logger.warn("Assignment creation failed: time conflict detected");
            return false;
        }
        
        assignment.setId(nextId++);
        assignment.setGateId(assignment.getGateId()); // Ensure gateId is set correctly
        assignment.initializeTimestamps();
        assignmentsByGate.computeIfAbsent(assignment.getGateId(), 
                                        k -> new ArrayList<>()).add(assignment);
        
        logger.info("Assignment created successfully for gate: {}", assignment.getGateId());
        return true;
    }
    
    /**
     * Updates an existing assignment's status.
     */
    public boolean updateAssignmentStatus(String gateId, Long assignmentId, 
                                        AssignmentStatus newStatus) {
        return updateAssignmentField(gateId, assignmentId, assignment -> assignment.updateStatus(newStatus));
    }

    /**
     * Deletes an assignment from a specific gate.
     * @param gateId The ID of the gate containing the assignment
     * @param assignmentId The ID of the assignment to delete
     * @return true if successfully deleted, false if not found
     */
    public boolean deleteAssignment(String gateId, Long assignmentId) {
        logger.info("Attempting to delete assignment {} from gate {}", assignmentId, gateId);
        
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            boolean removed = assignments.removeIf(a -> a.getId().equals(assignmentId));
            if (removed) {
                logger.info("Assignment successfully deleted");
                return true;
            }
        }
        
        logger.warn("Assignment not found for deletion");
        return false;
    }

    /**
     * Updates an existing assignment.
     * @param gateId The ID of the gate containing the assignment
     * @param assignmentId The ID of the assignment to update
     * @param updated The updated assignment data
     * @return true if successfully updated, false if not found or conflict exists
     */
    public boolean updateAssignment(String gateId, Long assignmentId, AssignmentModel updated) {
        logger.info("Attempting to update assignment {} for gate {}", assignmentId, gateId);
        
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            // Find the existing assignment
            Optional<AssignmentModel> existing = assignments.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();
                
            if (existing.isPresent()) {
                // Check for conflicts with other assignments
                boolean hasConflict = assignments.stream()
                    .filter(a -> !a.getId().equals(assignmentId)) // Exclude current assignment
                    .anyMatch(a -> !a.isCancelled() && a.hasConflict(updated));
                    
                if (hasConflict) {
                    logger.warn("Update failed: time conflict detected");
                    return false;
                }
                
                // Update the assignment
                updated.setId(assignmentId); // Ensure ID is preserved
                updated.setUpdatedAt(LocalDateTime.now());
                assignments.set(assignments.indexOf(existing.get()), updated);
                
                logger.info("Assignment successfully updated");
                return true;
            }
        }
        
        logger.warn("Assignment not found for update");
        return false;
    }

    /**
     * Helper method to update a field of an assignment.
     */
    private boolean updateAssignmentField(String gateId, Long assignmentId, java.util.function.Consumer<AssignmentModel> updater) {
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            for (AssignmentModel assignment : assignments) {
                if (assignment.getId().equals(assignmentId)) {
                    updater.accept(assignment);
                    return true;
                }
            }
        }
        return false;
    }

    
}