package com.gcu.agms.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.AssignmentModel;
import com.gcu.agms.model.AssignmentStatus;

import jakarta.annotation.PostConstruct;

/**
 * In-memory implementation of assignment management.
 * This service maintains gate assignments and their statuses in memory,
 * providing CRUD operations and status management functionality.
 */
@Service
public class InMemoryAssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryAssignmentService.class);
    
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
                             LocalDateTime.now().plusHours(3), "system");
        createSampleAssignment("T2G3", "UA456", LocalDateTime.now().plusHours(2), 
                             LocalDateTime.now().plusHours(4), "system");
        createSampleAssignment("T3G2", "DL789", LocalDateTime.now().plusHours(3), 
                             LocalDateTime.now().plusHours(5), "system");
                             
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
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            for (AssignmentModel assignment : assignments) {
                if (assignment.getId().equals(assignmentId)) {
                    assignment.updateStatus(newStatus);
                    return true;
                }
            }
        }
        return false;
    }
}