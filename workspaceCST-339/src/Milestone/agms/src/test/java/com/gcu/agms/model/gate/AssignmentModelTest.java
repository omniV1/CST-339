package com.gcu.agms.model.gate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Assignment Model Tests")
class AssignmentModelTest {

    @Test
    @DisplayName("Should detect assignment conflicts")
    void testAssignmentConflicts() {
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setStartTime(LocalDateTime.now().plusHours(1));
        assignment1.setEndTime(LocalDateTime.now().plusHours(2));

        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setStartTime(LocalDateTime.now().plusMinutes(90)); // Overlaps
        assignment2.setEndTime(LocalDateTime.now().plusHours(3));

        assertTrue(assignment1.hasConflict(assignment2));
    }

    @Test
    @DisplayName("Should handle cancelled assignments")
    void testCancelledAssignments() {
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setStartTime(LocalDateTime.now().plusHours(1));
        assignment1.setEndTime(LocalDateTime.now().plusHours(2));
        assignment1.setCancelled(true);

        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setStartTime(LocalDateTime.now().plusMinutes(90));
        assignment2.setEndTime(LocalDateTime.now().plusHours(3));

        assertFalse(assignment1.hasConflict(assignment2));
    }

    @Test
    @DisplayName("Should correctly identify active assignments")
    void testActiveAssignments() {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setStartTime(LocalDateTime.now().minusMinutes(30));
        assignment.setEndTime(LocalDateTime.now().plusHours(1));
        
        assertTrue(assignment.isActive());
    }

    @Test
    @DisplayName("Should handle status updates with timestamps")
    void testStatusUpdates() {
        AssignmentModel assignment = new AssignmentModel();
        LocalDateTime testTime = LocalDateTime.of(2024, 2, 22, 10, 0);
        
        // Use a clock for deterministic testing
        Clock fixedClock = Clock.fixed(testTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        assignment.setClock(fixedClock);
        
        assignment.updateStatus(AssignmentStatus.ACTIVE);
        
        assertEquals(AssignmentStatus.ACTIVE, assignment.getStatus());
        assertNotNull(assignment.getUpdatedAt());
        assertEquals(testTime, assignment.getUpdatedAt(), 
            "Updated timestamp should match test time");
    }

    @Test
    @DisplayName("Should initialize timestamps correctly")
    void testTimestampInitialization() {
        AssignmentModel assignment = new AssignmentModel();
        LocalDateTime testTime = LocalDateTime.of(2024, 2, 22, 10, 0);
        
        Clock fixedClock = Clock.fixed(testTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        assignment.setClock(fixedClock);
        
        assignment.initializeTimestamps();
        
        assertNotNull(assignment.getCreatedAt(), "Created timestamp should not be null");
        assertNotNull(assignment.getUpdatedAt(), "Updated timestamp should not be null");
        assertEquals(testTime, assignment.getCreatedAt(), 
            "Created timestamp should match test time");
        assertEquals(testTime, assignment.getUpdatedAt(),
            "Updated timestamp should match test time");
    }

    @Test
    @DisplayName("Should not override existing creation timestamp")
    void testTimestampPreservation() {
        AssignmentModel assignment = new AssignmentModel();
        LocalDateTime initial = LocalDateTime.now().minusDays(1);
        assignment.setCreatedAt(initial);
        
        assignment.initializeTimestamps();
        
        assertEquals(initial, assignment.getCreatedAt());
        assertTrue(assignment.getUpdatedAt().isAfter(initial));
    }

    @Test
    @DisplayName("Should get and set assignment properties correctly")
    void testAssignmentProperties() {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setId(1L);
        assignment.setGateId("T1G1");
        assignment.setFlightNumber("AA123");
        assignment.setAssignedBy("admin");
        assignment.setStatus(AssignmentStatus.SCHEDULED);
        
        assertEquals(1L, assignment.getId());
        assertEquals("T1G1", assignment.getGateId());
        assertEquals("AA123", assignment.getFlightNumber());
        assertEquals("admin", assignment.getAssignedBy());
        assertEquals(AssignmentStatus.SCHEDULED, assignment.getStatus());
    }

    @Test
    @DisplayName("Should handle status changes")
    void testStatusLabelsAndCssClasses() {
        // Test each status
        for (AssignmentStatus status : AssignmentStatus.values()) {
            assertNotNull(status.getLabel(), "Label should not be null for " + status);
            assertNotNull(status.getCssClass(), "CSS class should not be null for " + status);
        }
    }

    @Test
    @DisplayName("Should correctly identify inactive assignments")
    void testInactiveAssignments() {
        AssignmentModel assignment = new AssignmentModel();
        assignment.setStartTime(LocalDateTime.now().plusHours(1));
        assignment.setEndTime(LocalDateTime.now().plusHours(2));
        
        assertFalse(assignment.isActive(), "Future assignment should not be active");
        
        assignment.setStartTime(LocalDateTime.now().minusHours(2));
        assignment.setEndTime(LocalDateTime.now().minusHours(1));
        
        assertFalse(assignment.isActive(), "Past assignment should not be active");
    }

    @Test
    @DisplayName("Should compare assignments correctly")
    void testAssignmentComparison() {
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setStartTime(LocalDateTime.now());
        assignment1.setEndTime(LocalDateTime.now().plusHours(1));

        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setStartTime(LocalDateTime.now().plusHours(2));
        assignment2.setEndTime(LocalDateTime.now().plusHours(3));

        assertFalse(assignment1.hasConflict(assignment2), 
            "Non-overlapping assignments should not conflict");
    }

    @Test
    @DisplayName("Should handle invalid timestamps")
    void testInvalidTimestamps() {
        AssignmentModel assignment = new AssignmentModel();
        
        // Test null start time
        assignment.setStartTime(null);
        assignment.setEndTime(LocalDateTime.now().plusHours(1));
        assertFalse(assignment.isActive(), "Assignment with null start time should not be active");
        
        // Test null end time
        assignment.setStartTime(LocalDateTime.now().minusHours(1));
        assignment.setEndTime(null);
        assertFalse(assignment.isActive(), "Assignment with null end time should not be active");
        
        // Test both null
        assignment.setStartTime(null);
        assignment.setEndTime(null);
        assertFalse(assignment.isActive(), "Assignment with null timestamps should not be active");
    }

    @Test
    @DisplayName("Should handle conflict checks with null timestamps")
    void testConflictWithNullTimestamps() {
        AssignmentModel assignment1 = new AssignmentModel();
        assignment1.setStartTime(null);
        assignment1.setEndTime(null);

        AssignmentModel assignment2 = new AssignmentModel();
        assignment2.setStartTime(LocalDateTime.now());
        assignment2.setEndTime(LocalDateTime.now().plusHours(1));

        assertFalse(assignment1.hasConflict(assignment2), 
            "Assignment with null timestamps should not conflict");
        assertFalse(assignment2.hasConflict(assignment1), 
            "Assignment should not conflict with null timestamps");
    }
}
