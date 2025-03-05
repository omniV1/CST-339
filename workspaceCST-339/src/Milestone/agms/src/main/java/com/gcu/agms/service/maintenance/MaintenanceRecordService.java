package com.gcu.agms.service.maintenance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.maintenance.MaintenanceRecord;

/**
 * Service interface for maintenance record operations.
 * Defines methods for managing maintenance records.
 */
public interface MaintenanceRecordService {
    
    /**
     * Creates a new maintenance record.
     * 
     * @param record The maintenance record to create
     * @return The created maintenance record
     */
    MaintenanceRecord createMaintenanceRecord(MaintenanceRecord record);
    
    /**
     * Retrieves a maintenance record by its record ID.
     * 
     * @param recordId The record ID to find
     * @return Optional containing the maintenance record if found, empty Optional otherwise
     */
    Optional<MaintenanceRecord> getMaintenanceRecord(String recordId);
    
    /**
     * Retrieves all maintenance records.
     * 
     * @return List of all maintenance records
     */
    List<MaintenanceRecord> getAllMaintenanceRecords();
    
    /**
     * Retrieves maintenance records for a specific aircraft.
     * 
     * @param registrationNumber The aircraft registration number
     * @return List of maintenance records for the specified aircraft
     */
    List<MaintenanceRecord> getMaintenanceRecordsByAircraft(String registrationNumber);
    
    /**
     * Updates an existing maintenance record.
     * 
     * @param record The maintenance record with updated information
     * @return The updated maintenance record
     */
    MaintenanceRecord updateMaintenanceRecord(MaintenanceRecord record);
    
    /**
     * Updates the status of a maintenance record.
     * 
     * @param recordId The record ID of the maintenance record to update
     * @param status The new status
     * @return true if update was successful, false otherwise
     */
    boolean updateMaintenanceStatus(String recordId, MaintenanceRecord.MaintenanceStatus status);
    
    /**
     * Completes a maintenance record by setting its status to COMPLETED and recording the completion date.
     * 
     * @param recordId The record ID of the maintenance record to complete
     * @param completionDate The date and time of completion
     * @param notes Optional notes about the completion
     * @return true if completion was successful, false otherwise
     */
    boolean completeMaintenanceRecord(String recordId, LocalDateTime completionDate, String notes);
    
    /**
     * Deletes a maintenance record.
     * 
     * @param recordId The record ID of the maintenance record to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteMaintenanceRecord(String recordId);
}