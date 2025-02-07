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
        // Create some sample assignments for testing
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setGateId("T1G1");
        assignment1.setFlightNumber("AA123");
        assignment1.setStartTime(LocalDateTime.now().minusHours(1));
        assignment1.setEndTime(LocalDateTime.now().plusHours(1));
        createAssignment(assignment1);
        
        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setGateId("T2G1");
        assignment2.setFlightNumber("UA456");
        assignment2.setStartTime(LocalDateTime.now().plusHours(1));
        assignment2.setEndTime(LocalDateTime.now().plusHours(3));
        createAssignment(assignment2);
    }
    
    public boolean createAssignment(AssignmentModel assignment) {
        logger.info("Creating new assignment for gate: {}", assignment.getGateId());
        
        // Validate gate exists
        if (!gateManagementService.getGateById(assignment.getGateId()).isPresent()) {
            logger.warn("Gate not found: {}", assignment.getGateId());
            return false;
        }
        
        // Check for conflicts
        List<AssignmentModel> existingAssignments = getAssignmentsForGate(assignment.getGateId());
        boolean hasConflict = existingAssignments.stream()
            .anyMatch(existing -> !existing.isCancelled() && existing.hasConflict(assignment));
            
        if (hasConflict) {
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
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            for (int i = 0; i < assignments.size(); i++) {
                if (assignments.get(i).getId().equals(assignmentId)) {
                    assignments.set(i, updated);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean deleteAssignment(String gateId, Long assignmentId) {
        List<AssignmentModel> assignments = assignmentsByGate.get(gateId);
        if (assignments != null) {
            return assignments.removeIf(a -> a.getId().equals(assignmentId));
        }
        return false;
    }

    public Map<String, AssignmentModel> getCurrentAssignments() {
        Map<String, AssignmentModel> currentAssignments = new HashMap<>();
        
        assignmentsByGate.forEach((gateId, assignments) -> {
            assignments.stream()
                .filter(a -> !a.isCancelled() && a.isActive())
                .findFirst()
                .ifPresent(assignment -> currentAssignments.put(gateId, assignment));
        });
        
        return currentAssignments;
    }
    
}