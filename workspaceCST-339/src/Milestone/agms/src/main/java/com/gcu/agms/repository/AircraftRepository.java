package com.gcu.agms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.AircraftType;

/**
 * Repository interface for aircraft data access operations.
 * Defines methods for performing CRUD operations and specific queries related to aircraft.
 */
public interface AircraftRepository {
    
    /**
     * Retrieve all aircraft.
     * 
     * @return List of all aircraft
     */
    List<AircraftModel> findAll();
    
    /**
     * Find an aircraft by its database ID.
     * 
     * @param id The database ID to search for
     * @return Optional containing the aircraft if found, empty Optional otherwise
     */
    Optional<AircraftModel> findById(Long id);
    
    /**
     * Find an aircraft by its registration number.
     * 
     * @param registrationNumber The registration number to search for
     * @return Optional containing the aircraft if found, empty Optional otherwise
     */
    Optional<AircraftModel> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Find aircraft by type.
     * 
     * @param type The aircraft type to search for
     * @return List of aircraft with the specified type
     */
    List<AircraftModel> findByType(AircraftType type);
    
    /**
     * Find aircraft by status.
     * 
     * @param status The aircraft status to search for
     * @return List of aircraft with the specified status
     */
    List<AircraftModel> findByStatus(String status);
    
    /**
     * Find available aircraft (status is AVAILABLE).
     * 
     * @return List of available aircraft
     */
    List<AircraftModel> findAvailableAircraft();
    
    /**
     * Save an aircraft to the database.
     * If the aircraft has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param aircraft The aircraft to save
     * @return The saved aircraft with generated ID (for new records)
     */
    AircraftModel save(AircraftModel aircraft);
    
    /**
     * Delete an aircraft by its database ID.
     * 
     * @param id The ID of the aircraft to delete
     */
    void deleteById(Long id);
    
    /**
     * Update the status of an aircraft.
     * 
     * @param registrationNumber The registration number of the aircraft
     * @param status The new status
     * @param location The current location (optional)
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(String registrationNumber, String status, String location);
    
    /**
     * Update the next maintenance date for an aircraft.
     * 
     * @param registrationNumber The registration number of the aircraft
     * @param maintenanceDate The date of the next maintenance
     * @return true if update was successful, false otherwise
     */
    boolean updateMaintenanceDate(String registrationNumber, LocalDateTime maintenanceDate);
    
    /**
     * Count aircraft by status.
     * 
     * @param status The status to count
     * @return The number of aircraft with the specified status
     */
    int countByStatus(String status);
    
    /**
     * Count all aircraft.
     * 
     * @return The total number of aircraft
     */
    int countAll();
    
    /**
     * Check if an aircraft exists with the given registration number.
     * 
     * @param registrationNumber The registration number to check
     * @return true if an aircraft exists with the registration number, false otherwise
     */
    boolean existsByRegistrationNumber(String registrationNumber);
}