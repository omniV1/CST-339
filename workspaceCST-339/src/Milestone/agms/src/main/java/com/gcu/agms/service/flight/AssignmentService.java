package com.gcu.agms.service.flight;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.service.gate.GateManagementService;

import jakarta.annotation.PostConstruct;

/**
 * Service class responsible for managing gate assignments for flights.
 * This service handles the creation, retrieval, updating, and deletion of gate assignments,
 * as well as handling conflict checking and current assignment status.
 * 
 * The service maintains an in-memory storage of assignments organized by gate ID and provides
 * functionality to manage the lifecycle of gate assignments including:
 * - Creating new assignments with conflict checking
 * - Retrieving assignments for specific gates
 * - Getting current and upcoming assignments
 * - Updating existing assignments
 * - Deleting assignments
 * - Tracking current assignments across all gates
 *
 * @Service Spring Service annotation indicating this is a service component
 * @see AssignmentModel
 * @see GateManagementService
 */
@Service
public class AssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
    private static final String SYSTEM_USER = "system";  // Added constant
    
    private final GateManagementService gateManagementService;
    
    // In-memory storage for assignments
    private final Map<String, List<AssignmentModel>> assignmentsByGate = new HashMap<>();
    private Long nextId = 1L;
    
    public AssignmentService(GateManagementService gateManagementService) {
        this.gateManagementService = gateManagementService;
    }
    
    @PostConstruct
    public void initialize() {
        logger.info("Initializing AssignmentService with sample data");
        createSampleAssignments();
    }
    
    private void createSampleAssignments() {
        // Create some sample assignments for different gates
        createSampleAssignment("T1G1", "AA123", 
            LocalDateTime.now().plusHours(1), 
            LocalDateTime.now().plusHours(3), 
            SYSTEM_USER);  // Use constant

        createSampleAssignment("T2G3", "UA456", 
            LocalDateTime.now().plusHours(2), 
            LocalDateTime.now().plusHours(4), 
            SYSTEM_USER);  // Use constant

        createSampleAssignment("T3G2", "DL789", 
            LocalDateTime.now().plusHours(3), 
            LocalDateTime.now().plusHours(5), 
            SYSTEM_USER);  // Use constant

        logger.info("Sample assignments created");
    }
    
    private void createSampleAssignment(String gateId, String flightNumber, 
                                  LocalDateTime startTime, LocalDateTime endTime, 
                                  String createdBy) {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId(gateId);
        assignment.setFlightNumber(flightNumber);
        assignment.setStartTime(startTime);
        assignment.setEndTime(endTime);
        assignment.setCreatedBy(createdBy);
        assignment.setCreatedAt(LocalDateTime.now());
        
        createAssignment(assignment);
        logger.debug("Created sample assignment for gate {} and flight {}", gateId, flightNumber);
    }
    
    public boolean createAssignment(AssignmentModel assignment) {
        logger.info("Creating new assignment for gate: {}", assignment.getGateId());
        
        // Validate gate exists
        if (!gateManagementService.getGateById(assignment.getGateId()).isPresent()) {
            logger.warn("Gate not found: {}", assignment.getGateId());
            return false;
        }
        
        // Check for conflicts
        if (hasConflict(assignment)) {
            logger.warn("Time conflict detected for gate: {}", assignment.getGateId());
            return false;
        }
        
        // Initialize assignment
        assignment.setId(nextId++);
        assignment.initializeTimestamps();
        
        // Store assignment
        assignmentsByGate
            .computeIfAbsent(assignment.getGateId(), k -> new ArrayList<>())
            .add(assignment);
            
        logger.info("Assignment created successfully");
        return true;
    }
    
    private boolean hasConflict(AssignmentModel assignment) {
        List<AssignmentModel> existingAssignments = getAssignmentsForGate(assignment.getGateId());
        return existingAssignments.stream()
            .anyMatch(existing -> !existing.isCancelled() && existing.hasConflict(assignment));
    }
    
    public List<AssignmentModel> getAssignmentsForGate(String gateId) {
        return assignmentsByGate.getOrDefault(gateId, new ArrayList<>());
    }
    
    public Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId) {
        Map<String, AssignmentModel> result = new HashMap<>();
        List<AssignmentModel> assignments = getAssignmentsForGate(gateId);
        LocalDateTime now = LocalDateTime.now();
        
        // Find current assignment
        Optional<AssignmentModel> current = assignments.stream()
            .filter(a -> !a.isCancelled() && a.isActive())
            .findFirst();
            
        // Find next assignment
        Optional<AssignmentModel> next = assignments.stream()
            .filter(a -> !a.isCancelled() && 
                        a.getStartTime().isAfter(now))
            .min(Comparator.comparing(AssignmentModel::getStartTime));
            
        current.ifPresent(a -> result.put("current", a));
        next.ifPresent(a -> result.put("next", a));
        
        return result;
    }
    
    public boolean updateAssignment(String gateId, Long assignmentId, AssignmentModel updated) {
        logger.info("Attempting to update assignment {} for gate {}", assignmentId, gateId);
        
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            // Find existing assignment
            Optional<AssignmentModel> existing = assignments.stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst();
                
            if (existing.isPresent()) {
                // Check for conflicts with OTHER assignments (excluding current)
                boolean hasConflict = assignments.stream()
                    .filter(a -> !a.getId().equals(assignmentId)) // Exclude current assignment
                    .anyMatch(a -> !a.isCancelled() && a.hasConflict(updated));
                    
                if (hasConflict) {
                    logger.warn("Update failed: time conflict detected");
                    return false;
                }
                
                // Update the assignment
                updated.setId(assignmentId);
                updated.setUpdatedAt(LocalDateTime.now());
                int index = assignments.indexOf(existing.get());
                assignments.set(index, updated);
                
                logger.info("Assignment successfully updated");
                return true;
            }
        }
        
        logger.warn("Assignment not found for update");
        return false;
    }

    public boolean deleteAssignment(String gateId, Long assignmentId) {
        return updateAssignmentField(gateId, assignmentId, assignment -> 
            assignmentsByGate.get(gateId).remove(assignment));
    }

    protected boolean updateAssignmentField(String gateId, Long assignmentId, java.util.function.Consumer<AssignmentModel> updater) {
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

    public Map<String, AssignmentModel> getCurrentAssignments() {
        Map<String, AssignmentModel> currentAssignments = new HashMap<>();
        
        assignmentsByGate.forEach((gateId, assignments) -> assignments.stream()
            .filter(a -> !a.isCancelled() && a.isActive())
            .findFirst()
            .ifPresent(assignment -> currentAssignments.put(gateId, assignment)));
        
        return currentAssignments;
    }

    public Optional<AssignmentModel> getCurrentAssignment(String gateId) {
        List<AssignmentModel> assignments = getAssignmentsForGate(gateId);
        return assignments.stream()
            .filter(this::isCurrentAssignment)
            .findFirst();
    }

    protected boolean isCurrentAssignment(AssignmentModel assignment) {
        LocalDateTime now = LocalDateTime.now();
        return assignment.getStartTime().isBefore(now) && 
               assignment.getEndTime().isAfter(now);
    }
}