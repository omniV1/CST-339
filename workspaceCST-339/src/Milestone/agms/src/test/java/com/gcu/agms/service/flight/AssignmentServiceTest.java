package com.gcu.agms.service.flight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void setUp() {
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
}