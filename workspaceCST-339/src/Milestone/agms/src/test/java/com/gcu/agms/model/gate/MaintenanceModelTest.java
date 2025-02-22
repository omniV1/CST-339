package com.gcu.agms.model.gate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@DisplayName("Maintenance Model Tests")
class MaintenanceModelTest {

    @Test
    @DisplayName("Should create maintenance model with all properties")
    void testMaintenanceModelCreation() {
        MaintenanceModel model = new MaintenanceModel();
        LocalDateTime now = LocalDateTime.now();
        
        model.setId(1L);
        model.setAircraftRegistration("N12345");
        model.setStartTime(now);
        model.setEndTime(now.plusHours(2));
        model.setType(MaintenanceModel.MaintenanceType.ROUTINE);
        model.setDescription("Test maintenance");
        model.setLocation("Hangar A");
        model.setStatus(MaintenanceModel.MaintenanceStatus.SCHEDULED);
        model.setTechnician("John Doe");

        assertEquals(1L, model.getId());
        assertEquals("N12345", model.getAircraftRegistration());
        assertEquals(now, model.getStartTime());
        assertEquals(now.plusHours(2), model.getEndTime());
        assertEquals(MaintenanceModel.MaintenanceType.ROUTINE, model.getType());
        assertEquals("Test maintenance", model.getDescription());
        assertEquals("Hangar A", model.getLocation());
        assertEquals(MaintenanceModel.MaintenanceStatus.SCHEDULED, model.getStatus());
        assertEquals("John Doe", model.getTechnician());
    }

    @Test
    @DisplayName("Should verify maintenance type properties")
    void testMaintenanceTypes() {
        assertEquals("Routine maintenance check", 
            MaintenanceModel.MaintenanceType.ROUTINE.getDescription());
        assertEquals("Repair work", 
            MaintenanceModel.MaintenanceType.REPAIR.getDescription());
        assertEquals("Safety inspection", 
            MaintenanceModel.MaintenanceType.INSPECTION.getDescription());
        assertEquals("System upgrade", 
            MaintenanceModel.MaintenanceType.UPGRADE.getDescription());
    }

    @Test
    @DisplayName("Should verify maintenance status properties")
    void testMaintenanceStatuses() {
        assertEquals("Scheduled", 
            MaintenanceModel.MaintenanceStatus.SCHEDULED.getLabel());
        assertEquals("info", 
            MaintenanceModel.MaintenanceStatus.SCHEDULED.getCssClass());

        assertEquals("In Progress", 
            MaintenanceModel.MaintenanceStatus.IN_PROGRESS.getLabel());
        assertEquals("warning", 
            MaintenanceModel.MaintenanceStatus.IN_PROGRESS.getCssClass());

        assertEquals("Completed", 
            MaintenanceModel.MaintenanceStatus.COMPLETED.getLabel());
        assertEquals("success", 
            MaintenanceModel.MaintenanceStatus.COMPLETED.getCssClass());

        assertEquals("Cancelled", 
            MaintenanceModel.MaintenanceStatus.CANCELLED.getLabel());
        assertEquals("danger", 
            MaintenanceModel.MaintenanceStatus.CANCELLED.getCssClass());
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void testNullHandling() {
        MaintenanceModel model = new MaintenanceModel();
        
        assertNull(model.getId());
        assertNull(model.getAircraftRegistration());
        assertNull(model.getStartTime());
        assertNull(model.getEndTime());
        assertNull(model.getType());
        assertNull(model.getDescription());
        assertNull(model.getLocation());
        assertEquals(MaintenanceModel.MaintenanceStatus.SCHEDULED, model.getStatus());
        assertNull(model.getTechnician());
    }

    @Test
    @DisplayName("Should handle maintenance model initialization")
    void testMaintenanceModelInitialization() {
        MaintenanceModel model = new MaintenanceModel();
        
        // Test initial state
        assertNotNull(model);
        assertEquals(MaintenanceModel.MaintenanceStatus.SCHEDULED, model.getStatus());
        assertNull(model.getId());
        assertNull(model.getAircraftRegistration());
        assertNull(model.getType());
    }

    @Test
    @DisplayName("Should validate date constraints")
    void testDateValidation() {
        MaintenanceModel model = new MaintenanceModel();
        LocalDateTime now = LocalDateTime.now();
        
        model.setStartTime(now);
        model.setEndTime(now.plusHours(2));
        
        // Test valid duration
        assertTrue(model.getEndTime().isAfter(model.getStartTime()));
        
        // Test duration calculation
        assertEquals(2, ChronoUnit.HOURS.between(model.getStartTime(), model.getEndTime()));
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void testEqualsAndHashcode() {
        LocalDateTime now = LocalDateTime.now();
        
        MaintenanceModel model1 = new MaintenanceModel();
        model1.setId(1L);
        model1.setAircraftRegistration("N12345");
        model1.setStartTime(now);
        model1.setEndTime(now.plusHours(2));
        
        MaintenanceModel model2 = new MaintenanceModel();
        model2.setId(1L);
        model2.setAircraftRegistration("N12345");
        model2.setStartTime(now);
        model2.setEndTime(now.plusHours(2));
        
        // Test equals
        assertTrue(model1.equals(model1)); // Same object
        assertTrue(model1.equals(model2)); // Equal objects
        assertFalse(model1.equals(null)); // Null check
        assertFalse(model1.equals(new Object())); // Different type
        
        // Test hashCode
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    @DisplayName("Should validate all maintenance statuses")
    void testAllMaintenanceStatuses() {
        for (MaintenanceModel.MaintenanceStatus status : MaintenanceModel.MaintenanceStatus.values()) {
            assertNotNull(status.getLabel());
            assertNotNull(status.getCssClass());
            assertFalse(status.getLabel().isEmpty());
            assertFalse(status.getCssClass().isEmpty());
        }
    }

    @Test
    @DisplayName("Should validate all maintenance types")
    void testAllMaintenanceTypes() {
        for (MaintenanceModel.MaintenanceType type : MaintenanceModel.MaintenanceType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isEmpty());
        }
    }

    @Test
    @DisplayName("Should test toString method")
    void testToString() {
        MaintenanceModel model = new MaintenanceModel();
        model.setId(1L);
        model.setAircraftRegistration("N12345");
        model.setType(MaintenanceModel.MaintenanceType.ROUTINE);
        
        String toString = model.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("aircraftRegistration=N12345"));
        assertTrue(toString.contains("type=ROUTINE"));
    }
    
    @Test
    @DisplayName("Should handle status transitions")
    void testStatusTransitions() {
        MaintenanceModel model = new MaintenanceModel();
        
        assertEquals(MaintenanceModel.MaintenanceStatus.SCHEDULED, model.getStatus());
        
        model.setStatus(MaintenanceModel.MaintenanceStatus.IN_PROGRESS);
        assertEquals(MaintenanceModel.MaintenanceStatus.IN_PROGRESS, model.getStatus());
        
        model.setStatus(MaintenanceModel.MaintenanceStatus.COMPLETED);
        assertEquals(MaintenanceModel.MaintenanceStatus.COMPLETED, model.getStatus());
    }
}