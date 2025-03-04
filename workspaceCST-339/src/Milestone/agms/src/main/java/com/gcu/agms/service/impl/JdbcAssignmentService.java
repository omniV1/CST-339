package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;
import com.gcu.agms.repository.AssignmentRepository;
import com.gcu.agms.service.flight.AssignmentService;

/**
 * JDBC implementation of the AssignmentService interface.
 * This service uses a database repository for gate assignment operations.
 */
@Service("jdbcAssignmentService")
@Primary
public class JdbcAssignmentService implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAssignmentService.class);
    private final AssignmentRepository assignmentRepository;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param assignmentRepository Repository for assignment data access
     */
    public JdbcAssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
        logger.info("Initialized JDBC Assignment Service");
    }

    @Override
    public boolean createAssignment(AssignmentModel assignment) {
        logger.info("Creating new assignment for gate: {}", assignment.getGateId());
        
        // Initialize assignment if needed
        if (assignment.getStatus() == null) {
            assignment.setStatus(AssignmentStatus.SCHEDULED);
        }
        
        // Check for conflicts
        if (hasConflict(assignment)) {
            logger.warn("Time conflict detected for gate: {}", assignment.getGateId());
            return false;
        }
        
        // Add default values if not provided
        if (assignment.getAssignedBy() == null || assignment.getAssignedBy().trim().isEmpty()) {
            assignment.setAssignedBy("system");
        }
        
        if (assignment.getCreatedBy() == null || assignment.getCreatedBy().trim().isEmpty()) {
            assignment.setCreatedBy("system");
        }
        
        // Initialize timestamps if needed
        assignment.initializeTimestamps();
        
        // Save assignment
        AssignmentModel savedAssignment = assignmentRepository.save(assignment);
        logger.info("Assignment created successfully with ID: {}", savedAssignment.getId());
        
        return savedAssignment.getId() != null;
    }

    @Override
    public List<AssignmentModel> getAssignmentsForGate(String gateId) {
        logger.debug("Retrieving assignments for gate: {}", gateId);
        return assignmentRepository.findByGateId(gateId);
    }

    @Override
    public Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId) {
        logger.debug("Retrieving current and next assignments for gate: {}", gateId);
        return assignmentRepository.getCurrentAndNextAssignments(gateId);
    }

    @Override
    public boolean updateAssignment(String gateId, Long assignmentId, AssignmentModel updated) {
        logger.info("Updating assignment {} for gate {}", assignmentId, gateId);
        
        // Verify the assignment exists and belongs to this gate
        Optional<AssignmentModel> existingOpt = assignmentRepository.findById(assignmentId);
        if (existingOpt.isEmpty() || !existingOpt.get().getGateId().equals(gateId)) {
            logger.warn("Assignment not found or doesn't belong to gate {}", gateId);
            return false;
        }
        
        // Ensure the updated assignment still belongs to the same gate
        if (!updated.getGateId().equals(gateId)) {
            logger.warn("Cannot change gate ID during assignment update");
            updated.setGateId(gateId);
        }
        
        // Check for conflicts with other assignments
        if (hasConflict(updated)) {
            logger.warn("Update failed: time conflict detected");
            return false;
        }
        
        // Copy ID and other fields that shouldn't change
        AssignmentModel existing = existingOpt.get();
        updated.setId(assignmentId);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setCreatedBy(existing.getCreatedBy());
        updated.setUpdatedAt(LocalDateTime.now());
        
        // Save updated assignment
        assignmentRepository.save(updated);
        logger.info("Assignment successfully updated");
        
        return true;
    }

    @Override
    public boolean deleteAssignment(String gateId, Long assignmentId) {
        logger.info("Deleting assignment {} from gate {}", assignmentId, gateId);
        
        // Verify the assignment exists and belongs to this gate
        Optional<AssignmentModel> existingOpt = assignmentRepository.findById(assignmentId);
        if (existingOpt.isEmpty() || !existingOpt.get().getGateId().equals(gateId)) {
            logger.warn("Assignment not found or doesn't belong to gate {}", gateId);
            return false;
        }
        
        return assignmentRepository.deleteById(assignmentId);
    }

    @Override
    public boolean updateAssignmentField(String gateId, Long assignmentId, java.util.function.Consumer<AssignmentModel> updater) {
        logger.debug("Updating field for assignment {} of gate {}", assignmentId, gateId);
        
        // Verify the assignment exists and belongs to this gate
        Optional<AssignmentModel> existingOpt = assignmentRepository.findById(assignmentId);
        if (existingOpt.isEmpty() || !existingOpt.get().getGateId().equals(gateId)) {
            logger.warn("Assignment not found or doesn't belong to gate {}", gateId);
            return false;
        }
        
        // Apply the update and save
        AssignmentModel assignment = existingOpt.get();
        updater.accept(assignment);
        assignment.setUpdatedAt(LocalDateTime.now());
        
        assignmentRepository.save(assignment);
        return true;
    }

    @Override
    public Map<String, AssignmentModel> getCurrentAssignments() {
        logger.debug("Retrieving current assignments for all gates");
        
        Map<String, AssignmentModel> currentAssignments = new HashMap<>();
        List<AssignmentModel> activeAssignments = assignmentRepository.findActiveAssignments();
        
        // Group by gate ID and take the first one for each gate (there should only be one active per gate)
        Map<String, List<AssignmentModel>> assignmentsByGate = new HashMap<>();
        for (AssignmentModel assignment : activeAssignments) {
            assignmentsByGate.computeIfAbsent(assignment.getGateId(), k -> new ArrayList<>())
                            .add(assignment);
        }
        
        // Take the first assignment for each gate
        for (Map.Entry<String, List<AssignmentModel>> entry : assignmentsByGate.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                currentAssignments.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        
        return currentAssignments;
    }

    @Override
    public Optional<AssignmentModel> getCurrentAssignment(String gateId) {
        logger.debug("Retrieving current assignment for gate: {}", gateId);
        
        Map<String, AssignmentModel> currentAndNext = assignmentRepository.getCurrentAndNextAssignments(gateId);
        return Optional.ofNullable(currentAndNext.get("current"));
    }

    @Override
    public boolean isCurrentAssignment(AssignmentModel assignment) {
        if (assignment == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return assignment.getStartTime().isBefore(now) && 
               assignment.getEndTime().isAfter(now) &&
               !assignment.isCancelled();
    }

    @Override
    public boolean hasConflict(AssignmentModel assignment) {
        return assignmentRepository.hasConflict(assignment);
    }
}