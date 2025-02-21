package com.gcu.agms.service.impl;

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

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.AircraftType;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.maintenance.MaintenanceRecord;

@DisplayName("InMemoryFlightOperationsService Tests")
class InMemoryFlightOperationsServiceTest {
    
    private InMemoryFlightOperationsService flightOperationsService;

    @BeforeEach
    void setUp() {
        flightOperationsService = new InMemoryFlightOperationsService();
        flightOperationsService.initialize();
    }

    @Test
    @DisplayName("Should register aircraft successfully")
    void testRegisterAircraft() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        assertTrue(flightOperationsService.registerAircraft(aircraft));
        
        Optional<AircraftModel> found = flightOperationsService.getAircraft("TEST123");
        assertTrue(found.isPresent());
        assertEquals("Boeing 737-800", found.get().getModel());
    }

    @Test
    @DisplayName("Should not register duplicate aircraft")
    void testRegisterDuplicateAircraft() {
        AircraftModel aircraft = new AircraftModel("TEST123", "Boeing 737-800", AircraftType.NARROW_BODY);
        assertTrue(flightOperationsService.registerAircraft(aircraft));
        assertFalse(flightOperationsService.registerAircraft(aircraft));
    }

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
}