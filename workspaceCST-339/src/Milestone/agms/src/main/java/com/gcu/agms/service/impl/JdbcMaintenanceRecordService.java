package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.repository.MaintenanceRecordRepository;
import com.gcu.agms.service.maintenance.MaintenanceRecordService;

/**
 * JDBC implementation of the MaintenanceRecordService interface.
 * This service uses a database repository for maintenance record operations.
 */
@Service("jdbcMaintenanceRecordService")
@Primary
public class JdbcMaintenanceRecordService implements MaintenanceRecordService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMaintenanceRecordService.class);
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param maintenanceRecordRepository Repository for maintenance record data access
     */
    public JdbcMaintenanceRecordService(MaintenanceRecordRepository maintenanceRecordRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        logger.info("Initialized JDBC Maintenance Record Service");
    }

    @Override
    public MaintenanceRecord createMaintenanceRecord(MaintenanceRecord record) {
        logger.info("Creating new maintenance record for aircraft: {}", record.getRegistrationNumber());
        
        // Validate required fields
        if (record.getRegistrationNumber() == null || record.getScheduledDate() == null || 
            record.getType() == null || record.getDescription() == null) {
            logger.warn("Invalid maintenance record data - missing required fields");
            return null;
        }
        
        // Generate record ID if not provided
        if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
            record.setRecordId(UUID.randomUUID().toString());
        }
        
        // Set default status if not provided
        if (record.getStatus() == null) {
            record.setStatus(MaintenanceRecord.MaintenanceStatus.SCHEDULED);
        }
        
        // Save the record
        try {
            MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);
            logger.info("Maintenance record created successfully with ID: {}", savedRecord.getRecordId());
            return savedRecord;
        } catch (Exception e) {
            logger.error("Error creating maintenance record: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Optional<MaintenanceRecord> getMaintenanceRecord(String recordId) {
        logger.debug("Retrieving maintenance record with ID: {}", recordId);
        return maintenanceRecordRepository.findByRecordId(recordId);
    }

    @Override
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        logger.debug("Retrieving all maintenance records");
        return maintenanceRecordRepository.findAll();
    }

    @Override
    public List<MaintenanceRecord> getMaintenanceRecordsByAircraft(String registrationNumber) {
        logger.debug("Retrieving maintenance records for aircraft: {}", registrationNumber);
        return maintenanceRecordRepository.findByRegistrationNumber(registrationNumber);
    }

    @Override
    public MaintenanceRecord updateMaintenanceRecord(MaintenanceRecord record) {
        logger.info("Updating maintenance record: {}", record.getRecordId());
        
        // Validate record ID
        if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
            logger.warn("Cannot update maintenance record without record ID");
            return null;
        }
        
        // Check if record exists
        if (maintenanceRecordRepository.findByRecordId(record.getRecordId()).isEmpty()) {
            logger.warn("Maintenance record not found with ID: {}", record.getRecordId());
            return null;
        }
        
        // Save the updated record
        try {
            MaintenanceRecord updatedRecord = maintenanceRecordRepository.save(record);
            logger.info("Maintenance record updated successfully: {}", updatedRecord.getRecordId());
            return updatedRecord;
        } catch (Exception e) {
            logger.error("Error updating maintenance record: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean updateMaintenanceStatus(String recordId, MaintenanceRecord.MaintenanceStatus status) {
        logger.info("Updating status for maintenance record: {} to {}", recordId, status);
        
        Optional<MaintenanceRecord> recordOpt = maintenanceRecordRepository.findByRecordId(recordId);
        if (recordOpt.isEmpty()) {
            logger.warn("Maintenance record not found with ID: {}", recordId);
            return false;
        }
        
        MaintenanceRecord record = recordOpt.get();
        record.setStatus(status);
        
        // If status is COMPLETED and completion date is not set, set it to now
        if (status == MaintenanceRecord.MaintenanceStatus.COMPLETED && record.getCompletionDate() == null) {
            record.setCompletionDate(LocalDateTime.now());
        }
        
        try {
            maintenanceRecordRepository.save(record);
            logger.info("Maintenance record status updated successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error updating maintenance record status: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean completeMaintenanceRecord(String recordId, LocalDateTime completionDate, String notes) {
        logger.info("Completing maintenance record: {}", recordId);
        
        Optional<MaintenanceRecord> recordOpt = maintenanceRecordRepository.findByRecordId(recordId);
        if (recordOpt.isEmpty()) {
            logger.warn("Maintenance record not found with ID: {}", recordId);
            return false;
        }
        
        MaintenanceRecord record = recordOpt.get();
        record.setStatus(MaintenanceRecord.MaintenanceStatus.COMPLETED);
        record.setCompletionDate(completionDate != null ? completionDate : LocalDateTime.now());
        
        if (notes != null && !notes.trim().isEmpty()) {
            record.setNotes(notes);
        }
        
        try {
            maintenanceRecordRepository.save(record);
            logger.info("Maintenance record completed successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error completing maintenance record: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteMaintenanceRecord(String recordId) {
        logger.info("Deleting maintenance record: {}", recordId);
        return maintenanceRecordRepository.deleteByRecordId(recordId);
    }
}