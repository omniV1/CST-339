package com.gcu.agms.service.flight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.FlightModel;

/**
 * Service interface defining flight operations functionality.
 * This interface handles both aircraft and flight management operations.
 */
public interface FlightOperationsService {
    // Aircraft Management
    /**
     * Registers a new aircraft in the system
     * @param aircraft The aircraft to register
     * @return true if registration was successful, false if aircraft already exists
     */
    boolean registerAircraft(AircraftModel aircraft);

    /**
     * Retrieves an aircraft by its registration number
     * @param registrationNumber The unique registration number of the aircraft
     * @return Optional containing the aircraft if found
     */
    Optional<AircraftModel> getAircraft(String registrationNumber);

    /**
     * Gets all registered aircraft
     * @return List of all aircraft in the system
     */
    List<AircraftModel> getAllAircraft();

    /**
     * Updates aircraft status and location
     */
    boolean updateAircraftStatus(String registrationNumber, 
                               AircraftModel.AircraftStatus newStatus, 
                               String location);

    /**
     * Schedules maintenance for an aircraft
     * @param registrationNumber The aircraft's registration number
     * @param maintenanceDate The scheduled maintenance date
     * @param maintenanceType The type of maintenance
     * @param description Description of the maintenance work
     * @return true if scheduled successfully, false otherwise
     */
    boolean scheduleMaintenance(String registrationNumber, 
                              LocalDateTime maintenanceDate,
                              String maintenanceType,
                              String description);

    // Flight Management
    /**
     * Creates or updates flight information
     */
    boolean updateFlight(FlightModel flight);

    /**
     * Gets detailed information about a specific flight
     */
    Map<String, Object> getFlightDetails(String flightNumber);

    /**
     * Retrieves all currently active flights
     */
    List<Map<String, Object>> getActiveFlights();

    /**
     * Gets operational statistics about flights and aircraft
     */
    Map<String, Integer> getOperationalStatistics();

     /**
     * Updates the status of a specific flight
     * @param flightNumber The unique identifier of the flight to update
     * @param status The new status to set for the flight
     * @param location The current location or gate (optional)
     * @return true if the update was successful, false otherwise
     */
    boolean updateFlightStatus(String flightNumber, String status, String location);
    
    /**
     * Deletes a flight from the system
     * @param flightNumber The flight number to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteFlight(String flightNumber);
    
}