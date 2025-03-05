package com.gcu.agms.repository;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.maintenance.MaintenanceRecord;

/**
 * Repository interface for maintenance record data access operations.
 * Defines methods for performing CRUD operations and specific queries related to maintenance records.
 */
public interface MaintenanceRecordRepository {
    
    /**
     * Retrieve all maintenance records.
     * 
     * @return List of all maintenance records
     */
    List<MaintenanceRecord> findAll();
    
    /**
     * Find a maintenance record by its ID.
     * 
     * @param id The database ID to search for
     * @return Optional containing the maintenance record if found, empty Optional otherwise
     */
    Optional<MaintenanceRecord> findById(Long id);
    
    /**
     * Find a maintenance record by its record ID (UUID).
     * 
     * @param recordId The record ID to search for
     * @return Optional containing the maintenance record if found, empty Optional otherwise
     */
    Optional<MaintenanceRecord> findByRecordId(String recordId);
    
    /**
     * Find maintenance records by aircraft registration number.
     * 
     * @param registrationNumber The aircraft registration number to search for
     * @return List of maintenance records for the specified aircraft
     */
    List<MaintenanceRecord> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Find maintenance records by status.
     * 
     * @param status The status to search for
     * @return List of maintenance records with the specified status
     */
    List<MaintenanceRecord> findByStatus(String status);
    
    /**
     * Find maintenance records by type.
     * 
     * @param type The type to search for
     * @return List of maintenance records with the specified type
     */
    List<MaintenanceRecord> findByType(String type);
    
    /**
     * Save a maintenance record to the database.
     * If the record has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param record The maintenance record to save
     * @return The saved maintenance record with generated ID (for new records)
     */
    MaintenanceRecord save(MaintenanceRecord record);
    
    /**
     * Delete a maintenance record by its database ID.
     * 
     * @param id The ID of the maintenance record to delete
     */
    void deleteById(Long id);
    
    /**
     * Delete a maintenance record by its record ID (UUID).
     * 
     * @param recordId The record ID of the maintenance record to delete
     * @return true if deletion was successful, false if not found
     */
    boolean deleteByRecordId(String recordId);
    
    /**
     * Update the status of a maintenance record.
     * 
     * @param recordId The record ID of the maintenance record
     * @param status The new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(String recordId, String status);
    
    /**
     * Count maintenance records by status.
     * 
     * @param status The status to count
     * @return The number of maintenance records with the specified status
     */
    int countByStatus(String status);
    
    /**
     * Count all maintenance records.
     * 
     * @return The total number of maintenance records
     */
    int countAll();
}