package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

@DisplayName("InMemoryFlightOperations Service Tests")
class InMemoryFlightOperationsServiceTest {
    
    private InMemoryFlightOperationsService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryFlightOperationsService();
        // Remove initializeTestFlights call as it's not needed
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
        assertTrue(service.registerAircraft(aircraft));
        
        Optional<AircraftModel> found = service.getAircraft("TEST123");
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
        assertTrue(service.registerAircraft(aircraft));
        assertFalse(service.registerAircraft(aircraft));
    }

    /**
     * Validates aircraft status updates
     * Tests status management and location tracking
     */
    @Test
    @DisplayName("Should update aircraft status")
    void testUpdateAircraftStatus() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        service.registerAircraft(aircraft);
        
        assertTrue(service.updateAircraftStatus(
            "TEST123", 
            AircraftModel.AircraftStatus.MAINTENANCE, 
            "Gate A1"
        ));
        
        Optional<AircraftModel> updated = service.getAircraft("TEST123");
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
        service.registerAircraft(aircraft);
        
        LocalDateTime maintenanceDate = LocalDateTime.now().plusDays(1);
        assertTrue(service.scheduleMaintenance(
            "TEST123",
            maintenanceDate,
            "ROUTINE",  // Changed from ENGINE_CHECK to ROUTINE
            "Routine engine maintenance"
        ));
        
        List<MaintenanceRecord> records = service.getMaintenanceRecords("TEST123");
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
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertTrue(service.createFlight(flight));
        
        Map<String, Object> details = service.getFlightDetails("AA123");
        assertNotNull(details);
        assertEquals("AA123", ((FlightModel)details.get("flight")).getFlightNumber());
    }

    @Test
    @DisplayName("Should handle invalid aircraft registration")
    void testInvalidAircraftRegistration() {
        // Create empty aircraft without required fields
        AircraftModel emptyAircraft = new AircraftModel();
        // Should fail because registrationNumber is required
        assertFalse(service.registerAircraft(emptyAircraft));
        
        // Test null and nonexistent lookups
        Optional<AircraftModel> nullResult = service.getAircraft(null);
        assertFalse(nullResult.isPresent());
        Optional<AircraftModel> nonexistentResult = service.getAircraft("NONEXISTENT");
        assertFalse(nonexistentResult.isPresent());
    }

    @Test
    @DisplayName("Should handle invalid maintenance scheduling")
    void testInvalidMaintenanceScheduling() {
        assertFalse(service.scheduleMaintenance(
            "NONEXISTENT",
            LocalDateTime.now(),
            "ROUTINE",
            "Test maintenance"
        ));
        
        assertTrue(service.getMaintenanceRecords("NONEXISTENT").isEmpty());
    }

    @Test
    @DisplayName("Should handle invalid aircraft status update")
    void testInvalidAircraftStatusUpdate() {
        assertFalse(service.updateAircraftStatus(
            "NONEXISTENT",
            AircraftModel.AircraftStatus.MAINTENANCE,
            "Gate A1"
        ));
    }

    @Test
    @DisplayName("Should update flight details")
    void testUpdateFlight() {
        // Create initial flight
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertTrue(service.createFlight(flight));
        
        // Update flight
        flight.setDestination("SFO");
        assertTrue(service.updateFlight(flight));
        
        // Verify update
        Map<String, Object> details = service.getFlightDetails("AA123");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals("SFO", updated.getDestination());
        
        // Delete flight
        assertTrue(service.deleteFlight("AA123"));
        Map<String, Object> details2 = service.getFlightDetails("AA123"); 
        assertNull(details2.get("flight"));
    }

    @Test
    @DisplayName("Should handle flight status updates")
    void testUpdateFlightStatus() {
        // Create flight with all required fields
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        flight.setCurrentLocation("Gate A1");
        
        assertTrue(service.createFlight(flight));
        
        // Update status using FlightModel.FlightStatus
        assertTrue(service.updateFlightStatus("AA123", FlightModel.FlightStatus.DEPARTED.name(), "Gate A1"));
        
        // Verify status update
        Map<String, Object> details = service.getFlightDetails("AA123");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals(FlightModel.FlightStatus.DEPARTED, updated.getStatus());
    }

    @Test
    @DisplayName("Should handle invalid flight status updates")
    void testInvalidFlightUpdates() {
        assertFalse(service.updateFlightStatus("INVALID", FlightModel.FlightStatus.DELAYED.name(), "Gate A1"));
        assertFalse(service.updateFlightStatus(null, FlightModel.FlightStatus.DELAYED.name(), "Gate A1"));
    }

    @Test
    @DisplayName("Should create flight successfully")  
    void testCreateFlight() {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("TEST123");
        flight.setOrigin("PHX");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737"); 
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        flight.setAirlineCode("AA"); // Add required airline code

        assertTrue(service.createFlight(flight));
        
        Map<String, Object> details = service.getFlightDetails("TEST123");
        FlightModel saved = (FlightModel)details.get("flight");
        assertEquals("PHX", saved.getOrigin());
    }

    @Test
    @DisplayName("Should not create duplicate flight")
    void testCreateDuplicateFlight() {
        // Create initial flight
        FlightModel firstFlight = new FlightModel();
        firstFlight.setFlightNumber("AA101");
        firstFlight.setOrigin("PHX");
        firstFlight.setDestination("LAX");
        firstFlight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        firstFlight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        firstFlight.setAssignedAircraft("B737");
        firstFlight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        firstFlight.setAirlineCode("AA");
        
        // First creation should succeed
        assertTrue(service.createFlight(firstFlight), "First flight creation should succeed");
        
        // Try to create duplicate flight
        FlightModel duplicateFlight = new FlightModel();
        duplicateFlight.setFlightNumber("AA101"); // Same flight number
        duplicateFlight.setOrigin("PHX");
        duplicateFlight.setDestination("LAX");
        duplicateFlight.setScheduledDeparture(LocalDateTime.now().plusHours(3));
        duplicateFlight.setScheduledArrival(LocalDateTime.now().plusHours(5));
        duplicateFlight.setAssignedAircraft("B737");
        duplicateFlight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        duplicateFlight.setAirlineCode("AA");
        
        // Second creation should fail
        assertFalse(service.createFlight(duplicateFlight), 
            "Should not create duplicate flight with same flight number");
    }

    @Test
    @DisplayName("Should handle flight search by criteria")
    void testSearchFlights() {
        // Create test flights
        FlightModel flight1 = new FlightModel();
        flight1.setFlightNumber("AA101");
        flight1.setOrigin("PHX");
        flight1.setDestination("LAX");
        flight1.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight1.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight1.setAssignedAircraft("B737");
        flight1.setStatus(FlightModel.FlightStatus.SCHEDULED);
        flight1.setAirlineCode("AA");
        
        FlightModel flight2 = new FlightModel();
        flight2.setFlightNumber("AA102");
        flight2.setOrigin("PHX");
        flight2.setDestination("SFO");
        flight2.setScheduledDeparture(LocalDateTime.now().plusHours(3));
        flight2.setScheduledArrival(LocalDateTime.now().plusHours(5));
        flight2.setAssignedAircraft("B737");
        flight2.setStatus(FlightModel.FlightStatus.SCHEDULED);
        flight2.setAirlineCode("AA");
        
        assertTrue(service.createFlight(flight1));
        assertTrue(service.createFlight(flight2));
        
        // Test search by origin
        List<FlightModel> phxFlights = service.searchFlights("PHX", null, null);
        assertEquals(2, phxFlights.size(), "Should find all flights from PHX");
        
        // Test search by destination
        List<FlightModel> laxFlights = service.searchFlights(null, "LAX", null);
        assertEquals(1, laxFlights.size(), "Should find one flight to LAX");
        
        // Test search by airline
        List<FlightModel> aaFlights = service.searchFlights(null, null, "AA");
        assertEquals(2, aaFlights.size(), "Should find all AA flights");
    }

    @Test
    @DisplayName("Should handle flight time updates")
    void testUpdateFlightTimes() {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA101");
        flight.setOrigin("PHX");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        flight.setAirlineCode("AA");
        
        assertTrue(service.createFlight(flight));
        
        // Update times
        LocalDateTime newDeparture = LocalDateTime.now().plusHours(3);
        LocalDateTime newArrival = LocalDateTime.now().plusHours(5);
        flight.setScheduledDeparture(newDeparture);
        flight.setScheduledArrival(newArrival);
        
        assertTrue(service.updateFlight(flight));
        
        Map<String, Object> details = service.getFlightDetails("AA101");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals(newDeparture, updated.getScheduledDeparture());
        assertEquals(newArrival, updated.getScheduledArrival());
    }

    @Test
    @DisplayName("Should validate flight data")
    void testFlightValidation() {
        FlightModel invalidFlight = new FlightModel();
        // Missing required fields
        assertFalse(service.createFlight(invalidFlight), 
            "Should not create flight without required fields");
            
        // Invalid dates (arrival before departure)
        FlightModel badTimes = new FlightModel();
        badTimes.setFlightNumber("AA101");
        badTimes.setOrigin("PHX");
        badTimes.setDestination("LAX");
        badTimes.setScheduledDeparture(LocalDateTime.now().plusHours(4));
        badTimes.setScheduledArrival(LocalDateTime.now().plusHours(2));
        badTimes.setAssignedAircraft("B737");
        badTimes.setStatus(FlightModel.FlightStatus.SCHEDULED);
        badTimes.setAirlineCode("AA");
        
        assertFalse(service.createFlight(badTimes),
            "Should not create flight with invalid times");
    }

    @Test
    @DisplayName("Should handle bulk flight operations")
    void testBulkFlightOperations() {
        List<FlightModel> flights = new ArrayList<>();
        
        // Create multiple flights
        for (int i = 1; i <= 5; i++) {
            FlightModel flight = new FlightModel();
            flight.setFlightNumber("AA10" + i);
            flight.setOrigin("PHX");
            flight.setDestination("LAX");
            flight.setScheduledDeparture(LocalDateTime.now().plusHours(i));
            flight.setScheduledArrival(LocalDateTime.now().plusHours(i + 2));
            flight.setAssignedAircraft("B737");
            flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
            flight.setAirlineCode("AA");
            flights.add(flight);
        }
        
        // Test bulk create
        assertTrue(service.createFlights(flights));
        
        // Test bulk status update using Stream.toList()
        assertTrue(service.updateFlightStatuses(
            flights.stream()
                  .map(FlightModel::getFlightNumber)
                  .toList(),
            FlightModel.FlightStatus.DELAYED.name(),
            "Weather delay"
        ));
        
        // Verify all flights were updated
        for (FlightModel flight : flights) {
            Map<String, Object> details = service.getFlightDetails(flight.getFlightNumber());
            FlightModel updated = (FlightModel)details.get("flight");
            assertEquals(FlightModel.FlightStatus.DELAYED, updated.getStatus());
        }
    }

    @Test
    @DisplayName("Should handle additional flight validation cases")
    void testAdditionalFlightValidation() {
        // Test with same departure/arrival times
        FlightModel sameTimes = new FlightModel();
        sameTimes.setFlightNumber("AA111");
        sameTimes.setAirlineCode("AA");
        sameTimes.setOrigin("PHX");
        sameTimes.setDestination("LAX");
        LocalDateTime sameTime = LocalDateTime.now().plusHours(2);
        sameTimes.setScheduledDeparture(sameTime);
        sameTimes.setScheduledArrival(sameTime);
        sameTimes.setAssignedAircraft("B737");
        sameTimes.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertFalse(service.createFlight(sameTimes), 
            "Should not create flight with same departure and arrival times");
            
        // Test with arrival before departure
        FlightModel invalidTimes = new FlightModel();
        invalidTimes.setFlightNumber("AA112");
        invalidTimes.setAirlineCode("AA");
        invalidTimes.setOrigin("PHX");
        invalidTimes.setDestination("LAX");
        invalidTimes.setScheduledDeparture(LocalDateTime.now().plusHours(4));
        invalidTimes.setScheduledArrival(LocalDateTime.now().plusHours(2));
        invalidTimes.setAssignedAircraft("B737");
        invalidTimes.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertFalse(service.createFlight(invalidTimes),
            "Should not create flight with arrival before departure");
    }

    @Test
    @DisplayName("Should handle edge cases in flight status updates")
    void testFlightStatusEdgeCases() {
        // Setup test flight
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA113");
        flight.setAirlineCode("AA");
        flight.setOrigin("PHX");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertTrue(service.createFlight(flight));
        
        // Test invalid status
        assertFalse(service.updateFlightStatus("AA113", "INVALID_STATUS", "Gate A1"),
            "Should not update to invalid status");
            
        // Test null location
        assertTrue(service.updateFlightStatus("AA113", FlightModel.FlightStatus.DELAYED.name(), null),
            "Should update status with null location");
            
        // Test empty location
        assertTrue(service.updateFlightStatus("AA113", FlightModel.FlightStatus.DELAYED.name(), ""),
            "Should update status with empty location");
    }

    @Test
    @DisplayName("Should handle null and empty values in search")
    void testSearchEdgeCases() {
        // Create test flight
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA114");
        flight.setAirlineCode("AA");
        flight.setOrigin("PHX");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
        
        assertTrue(service.createFlight(flight));
        
        // Test null search parameters
        List<FlightModel> nullSearch = service.searchFlights(null, null, null);
        assertFalse(nullSearch.isEmpty(), "Should return all flights with null parameters");
        
        // Test empty string parameters
        List<FlightModel> emptySearch = service.searchFlights("", "", "");
        assertTrue(emptySearch.isEmpty(), "Should return no flights with empty parameters");
        
        // Test mixed null and valid parameters
        List<FlightModel> mixedSearch = service.searchFlights("PHX", null, null);
        assertFalse(mixedSearch.isEmpty(), "Should find flights with partial criteria");
    }

    @Test
    @DisplayName("Should handle operational statistics")
    void testOperationalStatistics() {
        // Create flights in different statuses
        FlightModel scheduledFlight = createTestFlight("AA201", FlightModel.FlightStatus.SCHEDULED);
        FlightModel delayedFlight = createTestFlight("AA202", FlightModel.FlightStatus.DELAYED);
        FlightModel departedFlight = createTestFlight("AA203", FlightModel.FlightStatus.DEPARTED); 
        FlightModel completedFlight = createTestFlight("AA204", FlightModel.FlightStatus.COMPLETED);
        
        service.createFlight(scheduledFlight);
        service.createFlight(delayedFlight);
        service.createFlight(departedFlight);
        service.createFlight(completedFlight);
        
        Map<String, Integer> stats = service.getOperationalStatistics();
        
        assertNotNull(stats);
        assertEquals(4, stats.get("totalFlights"), "Should have total flight count");
        assertEquals(3, stats.get("activeFlights"), "Should count active flights");
        assertEquals(1, stats.get("delayedFlights"), "Should count delayed flights");
        assertEquals(0, stats.get("maintenanceCount"), "Should count maintenance");
    }

    @Test
    @DisplayName("Should handle active flights retrieval")
    void testGetActiveFlights() {
        // Create mix of active and completed flights
        FlightModel activeFlight1 = createTestFlight("AA301", FlightModel.FlightStatus.SCHEDULED);
        FlightModel activeFlight2 = createTestFlight("AA302", FlightModel.FlightStatus.DELAYED);
        FlightModel completedFlight = createTestFlight("AA303", FlightModel.FlightStatus.COMPLETED);
        
        service.createFlight(activeFlight1);
        service.createFlight(activeFlight2);
        service.createFlight(completedFlight);
        
        List<Map<String, Object>> activeFlights = service.getActiveFlights();
        
        assertEquals(2, activeFlights.size(), "Should only return non-completed flights");
        assertTrue(activeFlights.stream()
            .map(flight -> ((FlightModel)flight.get("flight")).getFlightNumber())
            .anyMatch(num -> num.equals("AA301")));
    }

    // Helper method to create test flights
    private FlightModel createTestFlight(String flightNumber, FlightModel.FlightStatus status) {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber(flightNumber);
        flight.setAirlineCode("AA");
        flight.setOrigin("PHX");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusHours(2));
        flight.setScheduledArrival(LocalDateTime.now().plusHours(4));
        flight.setAssignedAircraft("B737");
        flight.setStatus(status);
        return flight;
    }

    @Test
    @DisplayName("Should handle invalid flight deletions")
    void testInvalidFlightDeletions() {
        assertFalse(service.deleteFlight(null), "Should handle null flight number");
        assertFalse(service.deleteFlight(""), "Should handle empty flight number");
        assertFalse(service.deleteFlight("NONEXISTENT"), "Should handle non-existent flight");
    }

    @Test
    @DisplayName("Should handle concurrent status updates")
    void testConcurrentStatusUpdates() {
        FlightModel flight = createTestFlight("AA401", FlightModel.FlightStatus.SCHEDULED);
        service.createFlight(flight);
        
        // Simulate concurrent updates
        assertTrue(service.updateFlightStatus("AA401", FlightModel.FlightStatus.BOARDING.name(), "Gate A1"));
        assertTrue(service.updateFlightStatus("AA401", FlightModel.FlightStatus.DEPARTED.name(), "Runway 1"));
        
        Map<String, Object> details = service.getFlightDetails("AA401");
        FlightModel updated = (FlightModel)details.get("flight");
        assertEquals(FlightModel.FlightStatus.DEPARTED, updated.getStatus());
        assertEquals("Runway 1", updated.getCurrentLocation());
    }

}