package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;

@DisplayName("InMemoryAssignmentService Tests")
class InMemoryAssignmentServiceTest {
    
    private InMemoryAssignmentService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryAssignmentService();
        service.initialize();
    }

    @Test
    @DisplayName("Should create new assignment")
    void testCreateAssignment() {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId("T4G1"); // Using a gate not in sample data
        assignment.setFlightNumber("AA999");
        assignment.setStartTime(LocalDateTime.now().plusHours(4));
        assignment.setEndTime(LocalDateTime.now().plusHours(6));
        assignment.setAssignedBy("test-user");
        assignment.setStatus(AssignmentStatus.SCHEDULED);

        assertTrue(service.createAssignment(assignment));
        
        List<AssignmentModel> assignments = service.getAssignmentsForGate("T4G1");
        assertFalse(assignments.isEmpty());
        assertEquals("AA999", assignments.get(0).getFlightNumber());
    }

    @Test
    @DisplayName("Should detect assignment conflicts")
    void testAssignmentConflict() {
        // Create first assignment
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setGateId("T4G2");
        assignment1.setFlightNumber("AA998");
        LocalDateTime baseTime = LocalDateTime.now();
        assignment1.setStartTime(baseTime.plusHours(1));
        assignment1.setEndTime(baseTime.plusHours(3));
        assignment1.setStatus(AssignmentStatus.SCHEDULED);
        
        assertTrue(service.createAssignment(assignment1));

        // Create conflicting assignment
        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setGateId("T4G2");
        assignment2.setFlightNumber("AA997");
        assignment2.setStartTime(baseTime.plusHours(2)); // Overlaps
        assignment2.setEndTime(baseTime.plusHours(4));
        assignment2.setStatus(AssignmentStatus.SCHEDULED);
        
        assertFalse(service.createAssignment(assignment2));
    }

    @Test
    @DisplayName("Should update assignment status")
    void testUpdateAssignmentStatus() {
        // Create new assignment
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId("T4G3");
        assignment.setFlightNumber("AA996");
        assignment.setStartTime(LocalDateTime.now().plusHours(1));
        assignment.setEndTime(LocalDateTime.now().plusHours(3));
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        
        assertTrue(service.createAssignment(assignment));
        
        // Get the assigned ID from the created assignment
        Long assignmentId = service.getAssignmentsForGate("T4G3").get(0).getId();
        
        // Update status
        assertTrue(service.updateAssignmentStatus("T4G3", assignmentId, AssignmentStatus.ACTIVE));
        
        // Verify update
        List<AssignmentModel> assignments = service.getAssignmentsForGate("T4G3");
        assertEquals(AssignmentStatus.ACTIVE, assignments.get(0).getStatus());
    }

    @Test
    @DisplayName("Should delete assignment")
    void testDeleteAssignment() {
        // Create assignment with unique gate ID
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId("T4G5"); // Using a gate not in sample data
        assignment.setFlightNumber("AA993");
        assignment.setStartTime(LocalDateTime.now().plusHours(1));
        assignment.setEndTime(LocalDateTime.now().plusHours(3));
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        
        // Create and verify assignment exists
        assertTrue(service.createAssignment(assignment));
        assertFalse(service.getAssignmentsForGate("T4G5").isEmpty());
        
        // Get the assigned ID and delete
        Long assignmentId = service.getAssignmentsForGate("T4G5").get(0).getId();
        assertTrue(service.deleteAssignment("T4G5", assignmentId));
        
        // Verify deletion
        assertTrue(service.getAssignmentsForGate("T4G5").isEmpty());
    }

    @Test
    @DisplayName("Should get current and next assignments")
    void testGetCurrentAndNextAssignments() {
        // Create current assignment
        AssignmentModel current = new AssignmentModel();
        current.setGateId("T4G4");
        current.setFlightNumber("AA995");
        current.setStartTime(LocalDateTime.now().minusHours(1));
        current.setEndTime(LocalDateTime.now().plusHours(1));
        current.setStatus(AssignmentStatus.ACTIVE);
        service.createAssignment(current);

        // Create next assignment
        AssignmentModel next = new AssignmentModel();
        next.setGateId("T4G4");
        next.setFlightNumber("AA994");
        next.setStartTime(LocalDateTime.now().plusHours(2));
        next.setEndTime(LocalDateTime.now().plusHours(4));
        next.setStatus(AssignmentStatus.SCHEDULED);
        service.createAssignment(next);

        Map<String, AssignmentModel> assignments = service.getCurrentAndNextAssignments("T4G4");
        
        assertNotNull(assignments.get("current"));
        assertNotNull(assignments.get("next"));
        assertEquals("AA995", assignments.get("current").getFlightNumber());
        assertEquals("AA994", assignments.get("next").getFlightNumber());
    }
}
