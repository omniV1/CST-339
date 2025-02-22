package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.AircraftType;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.maintenance.MaintenanceRecord;

/**
 * Test suite for InMemoryFlightOperationsService
 * Tests aircraft registration, maintenance, and flight operations
 */
@DisplayName("InMemoryFlightOperationsService Tests")
class InMemoryFlightOperationsServiceTest {
    
    private InMemoryFlightOperationsService flightOperationsService;

    @BeforeEach
    void setUp() {
        flightOperationsService = new InMemoryFlightOperationsService();
        flightOperationsService.initialize();
    }

    /**
     * Tests successful aircraft registration and retrieval
     * Verifies basic aircraft management functionality
     */
    @Test
    @DisplayName("Should register aircraft successfully")
    void testRegisterAircraft() {
        // AircraftModel(String registrationNumber, String model, AircraftType type)
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        assertTrue(flightOperationsService.registerAircraft(aircraft));
        
        Optional<AircraftModel> found = flightOperationsService.getAircraft("TEST123");
        assertTrue(found.isPresent());
        assertEquals("Boeing 737-800", found.get().getModel());
    }

    /**
     * Ensures duplicate aircraft registrations are prevented
     * Tests data integrity constraints
     */
    @Test
    @DisplayName("Should not register duplicate aircraft")
    void testRegisterDuplicateAircraft() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        assertTrue(flightOperationsService.registerAircraft(aircraft));
        assertFalse(flightOperationsService.registerAircraft(aircraft));
    }

    /**
     * Validates aircraft status updates
     * Tests status management and location tracking
     */
    @Test
    @DisplayName("Should update aircraft status")
    void testUpdateAircraftStatus() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        flightOperationsService.registerAircraft(aircraft);
        
        assertTrue(flightOperationsService.updateAircraftStatus(
            "TEST123", 
            AircraftModel.AircraftStatus.MAINTENANCE, 
            "Gate A1"
        ));
        
        Optional<AircraftModel> updated = flightOperationsService.getAircraft("TEST123");
        assertTrue(updated.isPresent());
        assertEquals(AircraftModel.AircraftStatus.MAINTENANCE, updated.get().getStatus());
        assertEquals("Gate A1", updated.get().getCurrentLocation());
    }

    /**
     * Tests maintenance scheduling functionality
     * Verifies maintenance record creation and retrieval
     */
    @Test
    @DisplayName("Should schedule maintenance")
    void testScheduleMaintenance() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        flightOperationsService.registerAircraft(aircraft);
        
        LocalDateTime maintenanceDate = LocalDateTime.now().plusDays(1);
        assertTrue(flightOperationsService.scheduleMaintenance(
            "TEST123",
            maintenanceDate,
            "ROUTINE",  // Changed from ENGINE_CHECK to ROUTINE
            "Routine engine maintenance"
        ));
        
        List<MaintenanceRecord> records = flightOperationsService.getMaintenanceRecords("TEST123");
        assertFalse(records.isEmpty());
        assertEquals("ROUTINE", records.get(0).getType().toString());
    }

    /**
     * Validates flight creation and retrieval
     * Tests basic flight management operations
     */
    @Test
    @DisplayName("Should create and retrieve flight")
    void testCreateAndRetrieveFlight() {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        
        assertTrue(flightOperationsService.createFlight(flight));
        
        Map<String, Object> details = flightOperationsService.getFlightDetails("AA123");
        assertNotNull(details);
        assertEquals("AA123", ((FlightModel)details.get("flight")).getFlightNumber());
    }

    @Test
    @DisplayName("Should handle invalid aircraft registration")
    void testInvalidAircraftRegistration() {
        // Create empty aircraft without required fields
        AircraftModel emptyAircraft = new AircraftModel();
        // Should fail because registrationNumber is required
        assertFalse(flightOperationsService.registerAircraft(emptyAircraft));
        
        // Test null and nonexistent lookups
        Optional<AircraftModel> nullResult = flightOperationsService.getAircraft(null);
        assertFalse(nullResult.isPresent());
        Optional<AircraftModel> nonexistentResult = flightOperationsService.getAircraft("NONEXISTENT");
        assertFalse(nonexistentResult.isPresent());
    }

    @Test
    @DisplayName("Should handle invalid maintenance scheduling")
    void testInvalidMaintenanceScheduling() {
        assertFalse(flightOperationsService.scheduleMaintenance(
            "NONEXISTENT",
            LocalDateTime.now(),
            "ROUTINE",
            "Test maintenance"
        ));
        
        assertTrue(flightOperationsService.getMaintenanceRecords("NONEXISTENT").isEmpty());
    }

    @Test
    @DisplayName("Should handle invalid aircraft status update")
    void testInvalidAircraftStatusUpdate() {
        assertFalse(flightOperationsService.updateAircraftStatus(
            "NONEXISTENT",
            AircraftModel.AircraftStatus.MAINTENANCE,
            "Gate A1"
        ));
    }


    @Test
    @DisplayName("Should update and delete flight")
    void testUpdateAndDeleteFlight() {
        // Create initial flight
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        assertTrue(flightOperationsService.createFlight(flight));
        
        // Update flight
        flight.setDestination("SFO");
        assertTrue(flightOperationsService.updateFlight(flight));
        
        // Verify update
        Map<String, Object> details = flightOperationsService.getFlightDetails("AA123");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals("SFO", updated.getDestination());
        
        // Delete flight
        assertTrue(flightOperationsService.deleteFlight("AA123"));
        Map<String, Object> details2 = flightOperationsService.getFlightDetails("AA123"); 
        assertNull(details2.get("flight"));
    }

    @Test
    @DisplayName("Should update flight status")
    void testUpdateFlightStatus() {
        // Create flight
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setCurrentLocation("Gate A1"); // Add current location
        assertTrue(flightOperationsService.createFlight(flight));
        
        // Update status
        assertTrue(flightOperationsService.updateFlightStatus("AA123", "DEPARTED", "Gate A1"));
        
        // Verify status update
        Map<String, Object> details = flightOperationsService.getFlightDetails("AA123");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals(FlightModel.FlightStatus.DEPARTED, updated.getStatus());
        assertEquals("Gate A1", updated.getCurrentLocation());
        
        // Test invalid status update
        assertFalse(flightOperationsService.updateFlightStatus("NONEXISTENT", "DEPARTED", "Gate A1"));
    }

    
}