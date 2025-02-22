package com.gcu.agms.service.flight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.maintenance.MaintenanceRecord;

/**
 * Service interface defining flight operations functionality.
 * This interface handles both aircraft and flight management operations.
 */
public interface FlightOperationsService {
    // Aircraft Management
    boolean registerAircraft(AircraftModel aircraft);
    Optional<AircraftModel> getAircraft(String registrationNumber);
    List<AircraftModel> getAllAircraft();
    boolean updateAircraftStatus(String registrationNumber, AircraftModel.AircraftStatus newStatus, String location);
    boolean scheduleMaintenance(String registrationNumber, LocalDateTime maintenanceDate, String maintenanceType, String description);
    List<MaintenanceRecord> getMaintenanceRecords(String registrationNumber);

    /**
     * Gets a list of all available aircraft (not in maintenance or currently assigned)
     * @return List of available aircraft
     */
    List<AircraftModel> getAvailableAircraft();

    // Flight Management
    boolean createFlight(FlightModel flight); // Add this method
    boolean updateFlight(FlightModel flight);
    Map<String, Object> getFlightDetails(String flightNumber);
    List<Map<String, Object>> getActiveFlights();
    Map<String, Integer> getOperationalStatistics();
    boolean updateFlightStatus(String flightNumber, String status, String location);
    boolean deleteFlight(String flightNumber);
}