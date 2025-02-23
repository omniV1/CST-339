package com.gcu.agms.service.flight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.gate.GateManagementService;

@DisplayName("Assignment Service Tests")
class AssignmentServiceTest {

    @Mock
    private GateManagementService gateManagementService;
    
    private AssignmentService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AssignmentService(gateManagementService);
    }

    @Test
    @DisplayName("Should create new assignment")
    void testCreateAssignment() {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId("T1G1");
        assignment.setFlightNumber("AA123");
        assignment.setStartTime(LocalDateTime.now().plusHours(1));
        assignment.setEndTime(LocalDateTime.now().plusHours(2));
        
        when(gateManagementService.getGateById("T1G1")).thenReturn(Optional.of(new GateModel()));
        
        assertTrue(service.createAssignment(assignment));
        
        List<AssignmentModel> assignments = service.getAssignmentsForGate("T1G1");
        assertEquals(1, assignments.size());
        assertEquals("AA123", assignments.get(0).getFlightNumber());
    }

    @Test
    @DisplayName("Should get current and next assignments")
    void testGetCurrentAndNextAssignments() {
        // Create test assignments
        AssignmentModel current = new AssignmentModel();
        current.setGateId("T1G1");
        current.setStartTime(LocalDateTime.now().minusHours(1));
        current.setEndTime(LocalDateTime.now().plusHours(1));
        
        AssignmentModel next = new AssignmentModel();
        next.setGateId("T1G1");
        next.setStartTime(LocalDateTime.now().plusHours(2));
        next.setEndTime(LocalDateTime.now().plusHours(3));
        
        // Mock gate existence for both assignments
        when(gateManagementService.getGateById("T1G1")).thenReturn(Optional.of(new GateModel()));
        
        // Create assignments
        assertTrue(service.createAssignment(current));
        assertTrue(service.createAssignment(next));
        
        // Get and verify assignments
        Map<String, AssignmentModel> assignments = service.getCurrentAndNextAssignments("T1G1");
        
        assertNotNull(assignments.get("current"));
        assertNotNull(assignments.get("next"));
        assertEquals(current.getFlightNumber(), assignments.get("current").getFlightNumber());
        assertEquals(next.getFlightNumber(), assignments.get("next").getFlightNumber());
    }

    @Test
    @DisplayName("Should update assignment")
    void testUpdateAssignment() {
        // Create initial assignment
        AssignmentModel assignment = new AssignmentModel();
        assignment.setGateId("T5G1");
        assignment.setFlightNumber("AA111");
        assignment.setStartTime(LocalDateTime.now().plusHours(1));
        assignment.setEndTime(LocalDateTime.now().plusHours(2));
        
        when(gateManagementService.getGateById("T5G1")).thenReturn(Optional.of(new GateModel()));
        assertTrue(service.createAssignment(assignment));
        
        // Create updated assignment
        AssignmentModel updated = new AssignmentModel();
        updated.setGateId("T5G1");
        updated.setFlightNumber("AA222"); 
        updated.setStartTime(LocalDateTime.now().plusHours(3));
        updated.setEndTime(LocalDateTime.now().plusHours(4));
        
        // Get ID of created assignment
        Long assignmentId = service.getAssignmentsForGate("T5G1").get(0).getId();
        
        // Update assignment
        assertTrue(service.updateAssignment("T5G1", assignmentId, updated));
        
        // Verify update
        List<AssignmentModel> assignments = service.getAssignmentsForGate("T5G1");
        assertEquals(1, assignments.size());
        assertEquals("AA222", assignments.get(0).getFlightNumber());
    }

    @Test
    @DisplayName("Should handle update of non-existent assignment") 
    void testUpdateNonExistentAssignment() {
        AssignmentModel updated = new AssignmentModel();
        updated.setGateId("T5G1");
        updated.setFlightNumber("AA222");
        assertFalse(service.updateAssignment("T5G1", 999L, updated));
    }

    @Test
    @DisplayName("Should get current assignments for all gates")
    void testGetCurrentAssignments() {
        // Create current assignment
        AssignmentModel current = new AssignmentModel();
        current.setGateId("T6G1");
        current.setFlightNumber("AA333");
        current.setStartTime(LocalDateTime.now().minusHours(1));
        current.setEndTime(LocalDateTime.now().plusHours(1));
        
        when(gateManagementService.getGateById("T6G1")).thenReturn(Optional.of(new GateModel()));
        assertTrue(service.createAssignment(current));
        
        // Get current assignments
        Map<String, AssignmentModel> currentAssignments = service.getCurrentAssignments();
        
        assertFalse(currentAssignments.isEmpty());
        assertEquals("AA333", currentAssignments.get("T6G1").getFlightNumber());
    }

    @Test
    @DisplayName("Should handle assignment update with conflicts")
    void testUpdateAssignmentWithConflict() {
        // Setup fixed test time
        LocalDateTime baseTime = LocalDateTime.now();
        
        // Create first assignment (1:00-2:00)
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setGateId("T7G1");
        assignment1.setFlightNumber("AA444");
        assignment1.setStartTime(baseTime.plusHours(1));
        assignment1.setEndTime(baseTime.plusHours(2));
        
        // Create second assignment (3:00-4:00)
        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setGateId("T7G1");
        assignment2.setFlightNumber("AA555");
        assignment2.setStartTime(baseTime.plusHours(3));
        assignment2.setEndTime(baseTime.plusHours(4));
        
        // Mock gate existence
        when(gateManagementService.getGateById("T7G1")).thenReturn(Optional.of(new GateModel()));
        
        // Create both assignments
        assertTrue(service.createAssignment(assignment1));
        assertTrue(service.createAssignment(assignment2));
        
        // Try to update second assignment to overlap with first (1:30-2:30)
        Long assignment2Id = service.getAssignmentsForGate("T7G1").get(1).getId();
        AssignmentModel conflictingUpdate = new AssignmentModel();
        conflictingUpdate.setGateId("T7G1");
        conflictingUpdate.setFlightNumber("AA555");
        conflictingUpdate.setStartTime(baseTime.plusMinutes(90));  // 1:30
        conflictingUpdate.setEndTime(baseTime.plusMinutes(150));   // 2:30
        
        // Should fail due to overlap with first assignment
        assertFalse(service.updateAssignment("T7G1", assignment2Id, conflictingUpdate));
    }

    @Test
    @DisplayName("Should handle delete of non-existent assignment")
    void testDeleteNonExistentAssignment() {
        assertFalse(service.deleteAssignment("T8G1", 999L));
    }

    @Test
    @DisplayName("Should handle multiple overlapping update attempts")
    void testMultipleOverlappingUpdates() {
        LocalDateTime baseTime = LocalDateTime.now();
        
        // Create initial assignments
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setGateId("T9G1");
        assignment1.setFlightNumber("AA777");
        assignment1.setStartTime(baseTime.plusHours(1)); // 1:00-2:00
        assignment1.setEndTime(baseTime.plusHours(2));
        
        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setGateId("T9G1"); 
        assignment2.setFlightNumber("AA888");
        assignment2.setStartTime(baseTime.plusHours(3)); // 3:00-4:00
        assignment2.setEndTime(baseTime.plusHours(4));
        
        when(gateManagementService.getGateById("T9G1")).thenReturn(Optional.of(new GateModel()));
        
        // Create both assignments
        assertTrue(service.createAssignment(assignment1));
        assertTrue(service.createAssignment(assignment2));
        
        // Try to update first assignment to overlap with second
        Long assignment1Id = service.getAssignmentsForGate("T9G1").get(0).getId();
        AssignmentModel conflictingUpdate1 = new AssignmentModel();
        conflictingUpdate1.setGateId("T9G1");
        conflictingUpdate1.setFlightNumber("AA777");
        conflictingUpdate1.setStartTime(baseTime.plusHours(2)); // 2:00-3:30
        conflictingUpdate1.setEndTime(baseTime.plusMinutes(210));
        
        // Should fail due to overlap
        assertFalse(service.updateAssignment("T9G1", assignment1Id, conflictingUpdate1));
        
        // Try non-conflicting update
        AssignmentModel validUpdate = new AssignmentModel();
        validUpdate.setGateId("T9G1");
        validUpdate.setFlightNumber("AA777");
        validUpdate.setStartTime(baseTime.plusMinutes(30)); // 0:30-1:30
        validUpdate.setEndTime(baseTime.plusMinutes(90));
        
        // Should succeed
        assertTrue(service.updateAssignment("T9G1", assignment1Id, validUpdate));
        
        // Verify update
        List<AssignmentModel> assignments = service.getAssignmentsForGate("T9G1");
        assertEquals(2, assignments.size());
        assertEquals(validUpdate.getStartTime(), assignments.get(0).getStartTime());
    }
}