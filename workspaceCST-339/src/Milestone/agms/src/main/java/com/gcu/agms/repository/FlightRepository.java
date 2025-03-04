package com.gcu.agms.repository;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.flight.FlightModel;

/**
 * Repository interface for flight data access operations.
 * Defines methods for performing CRUD operations and specific queries related to flights.
 */
public interface FlightRepository {
    
    /**
     * Retrieve all flights.
     * 
     * @return List of all flights
     */
    List<FlightModel> findAll();
    
    /**
     * Find a flight by its database ID.
     * 
     * @param id The database ID to search for
     * @return Optional containing the flight if found, empty Optional otherwise
     */
    Optional<FlightModel> findById(Long id);
    
    /**
     * Find a flight by its flight number.
     * 
     * @param flightNumber The flight number to search for
     * @return Optional containing the flight if found, empty Optional otherwise
     */
    Optional<FlightModel> findByFlightNumber(String flightNumber);
    
    /**
     * Find active flights (non-completed, non-cancelled).
     * 
     * @return List of active flights
     */
    List<FlightModel> findActiveFlights();
    
    /**
     * Find flights by status.
     * 
     * @param status The status to search for
     * @return List of flights with the specified status
     */
    List<FlightModel> findByStatus(String status);
    
    /**
     * Find flights by origin airport.
     * 
     * @param origin The origin airport code
     * @return List of flights departing from the specified airport
     */
    List<FlightModel> findByOrigin(String origin);
    
    /**
     * Find flights by destination airport.
     * 
     * @param destination The destination airport code
     * @return List of flights arriving at the specified airport
     */
    List<FlightModel> findByDestination(String destination);
    
    /**
     * Find flights by airline code.
     * 
     * @param airlineCode The airline code
     * @return List of flights operated by the specified airline
     */
    List<FlightModel> findByAirlineCode(String airlineCode);
    
    /**
     * Find flights by assigned aircraft.
     * 
     * @param aircraftRegistration The aircraft registration number
     * @return List of flights assigned to the specified aircraft
     */
    List<FlightModel> findByAssignedAircraft(String aircraftRegistration);
    
    /**
     * Save a flight to the database.
     * If the flight has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param flight The flight to save
     * @return The saved flight with generated ID (for new records)
     */
    FlightModel save(FlightModel flight);
    
    /**
     * Delete a flight by its database ID.
     * 
     * @param id The ID of the flight to delete
     */
    void deleteById(Long id);
    
    /**
     * Delete a flight by its flight number.
     * 
     * @param flightNumber The flight number of the flight to delete
     * @return true if flight was deleted, false if not found
     */
    boolean deleteByFlightNumber(String flightNumber);
    
    /**
     * Check if a flight exists with the given flight number.
     * 
     * @param flightNumber The flight number to check
     * @return true if a flight exists with the flight number, false otherwise
     */
    boolean existsByFlightNumber(String flightNumber);
    
    /**
     * Count flights by status.
     * 
     * @param status The status to count
     * @return The number of flights with the specified status
     */
    int countByStatus(String status);
    
    /**
     * Count flights excluding specified statuses.
     * 
     * @param statuses The statuses to exclude
     * @return The number of flights not having the specified statuses
     */
    int countByStatusNotIn(List<String> statuses);
    
    /**
     * Count all flights.
     * 
     * @return The total number of flights
     */
    int countAll();
    
    /**
     * Count active flights (non-completed, non-cancelled).
     * 
     * @return The number of active flights
     */
    int countActiveFlights();
    
    /**
     * Count delayed flights.
     * 
     * @return The number of delayed flights
     */
    int countDelayedFlights();
}