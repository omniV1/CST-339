package com.gcu.agms.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.repository.MaintenanceRecordRepository;

/**
 * JDBC implementation of the MaintenanceRecordRepository interface.
 * This class handles data access operations for maintenance records using Spring JDBC.
 */
@Repository
public class JdbcMaintenanceRecordRepository implements MaintenanceRecordRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMaintenanceRecordRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcMaintenanceRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized JdbcMaintenanceRecordRepository");
    }
    
    @Override
    public List<MaintenanceRecord> findAll() {
        logger.debug("Finding all maintenance records");
        String sql = "SELECT * FROM maintenance_record ORDER BY scheduled_date";
        
        try {
            return jdbcTemplate.query(sql, new MaintenanceRecordRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all maintenance records: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Optional<MaintenanceRecord> findById(Long id) {
        logger.debug("Finding maintenance record by ID: {}", id);
        String sql = "SELECT * FROM maintenance_record WHERE id = ?";
        
        try {
            List<MaintenanceRecord> results = jdbcTemplate.query(sql, new MaintenanceRecordRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No maintenance record found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding maintenance record by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<MaintenanceRecord> findByRecordId(String recordId) {
        logger.debug("Finding maintenance record by record ID: {}", recordId);
        String sql = "SELECT * FROM maintenance_record WHERE record_id = ?";
        
        try {
            List<MaintenanceRecord> results = jdbcTemplate.query(sql, new MaintenanceRecordRowMapper(), recordId);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No maintenance record found with record ID: {}", recordId);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding maintenance record by record ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<MaintenanceRecord> findByRegistrationNumber(String registrationNumber) {
        logger.debug("Finding maintenance records for aircraft: {}", registrationNumber);
        String sql = "SELECT * FROM maintenance_record WHERE registration_number = ? ORDER BY scheduled_date";
        
        try {
            return jdbcTemplate.query(sql, new MaintenanceRecordRowMapper(), registrationNumber);
        } catch (DataAccessException e) {
            logger.error("Database error finding maintenance records by registration number: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<MaintenanceRecord> findByStatus(String status) {
        logger.debug("Finding maintenance records by status: {}", status);
        String sql = "SELECT * FROM maintenance_record WHERE status = ? ORDER BY scheduled_date";
        
        try {
            return jdbcTemplate.query(sql, new MaintenanceRecordRowMapper(), status);
        } catch (DataAccessException e) {
            logger.error("Database error finding maintenance records by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<MaintenanceRecord> findByType(String type) {
        logger.debug("Finding maintenance records by type: {}", type);
        String sql = "SELECT * FROM maintenance_record WHERE type = ? ORDER BY scheduled_date";
        
        try {
            return jdbcTemplate.query(sql, new MaintenanceRecordRowMapper(), type);
        } catch (DataAccessException e) {
            logger.error("Database error finding maintenance records by type: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public MaintenanceRecord save(MaintenanceRecord record) {
        if (record.getRecordId() == null || findByRecordId(record.getRecordId()).isEmpty()) {
            // Insert new record
            return insertMaintenanceRecord(record);
        } else {
            // Update existing record
            return updateMaintenanceRecord(record);
        }
    }
    
    private MaintenanceRecord insertMaintenanceRecord(MaintenanceRecord record) {
        logger.debug("Inserting new maintenance record for aircraft: {}", record.getRegistrationNumber());
        
        String sql = "INSERT INTO maintenance_record (record_id, registration_number, scheduled_date, type, " +
                     "status, technician, description, completion_date, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                
                // Generate UUID if not provided
                if (record.getRecordId() == null || record.getRecordId().trim().isEmpty()) {
                    record.setRecordId(java.util.UUID.randomUUID().toString());
                }
                
                ps.setString(1, record.getRecordId());
                ps.setString(2, record.getRegistrationNumber());
                ps.setTimestamp(3, Timestamp.valueOf(record.getScheduledDate()));
                ps.setString(4, record.getType().name());
                ps.setString(5, record.getStatus().name());
                ps.setString(6, record.getTechnician());
                ps.setString(7, record.getDescription());
                
                // Handle nullable fields
                if (record.getCompletionDate() != null) {
                    ps.setTimestamp(8, Timestamp.valueOf(record.getCompletionDate()));
                } else {
                    ps.setNull(8, java.sql.Types.TIMESTAMP);
                }
                
                ps.setString(9, record.getNotes());
                
                // Set timestamps
                ps.setTimestamp(10, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(11, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                // If we had an ID field in MaintenanceRecord, we would set it here
                // record.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting maintenance record: {}", e.getMessage(), e);
        }
        
        return record;
    }
    
    private MaintenanceRecord updateMaintenanceRecord(MaintenanceRecord record) {
        logger.debug("Updating maintenance record: {}", record.getRecordId());
        
        String sql = "UPDATE maintenance_record SET registration_number = ?, scheduled_date = ?, type = ?, " +
                     "status = ?, technician = ?, description = ?, completion_date = ?, notes = ?, updated_at = ? " +
                     "WHERE record_id = ?";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            jdbcTemplate.update(
                sql,
                record.getRegistrationNumber(),
                Timestamp.valueOf(record.getScheduledDate()),
                record.getType().name(),
                record.getStatus().name(),
                record.getTechnician(),
                record.getDescription(),
                record.getCompletionDate() != null ? Timestamp.valueOf(record.getCompletionDate()) : null,
                record.getNotes(),
                Timestamp.valueOf(now),
                record.getRecordId()
            );
            
        } catch (DataAccessException e) {
            logger.error("Database error updating maintenance record: {}", e.getMessage(), e);
        }
        
        return record;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting maintenance record with ID: {}", id);
        
        String sql = "DELETE FROM maintenance_record WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting maintenance record: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteByRecordId(String recordId) {
        logger.debug("Deleting maintenance record with record ID: {}", recordId);
        
        String sql = "DELETE FROM maintenance_record WHERE record_id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, recordId);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error deleting maintenance record by record ID: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateStatus(String recordId, String status) {
        logger.debug("Updating status for maintenance record {}: {}", recordId, status);
        
        String sql = "UPDATE maintenance_record SET status = ?, updated_at = ? " +
                     "WHERE record_id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(
                sql,
                status,
                Timestamp.valueOf(LocalDateTime.now()),
                recordId
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error updating maintenance record status: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public int countByStatus(String status) {
        logger.debug("Counting maintenance records by status: {}", status);
        String sql = "SELECT COUNT(*) FROM maintenance_record WHERE status = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting maintenance records by status: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all maintenance records");
        String sql = "SELECT COUNT(*) FROM maintenance_record";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting all maintenance records: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Row mapper for converting database rows to MaintenanceRecord objects.
     */
    private static class MaintenanceRecordRowMapper implements RowMapper<MaintenanceRecord> {
        @Override
        public MaintenanceRecord mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            MaintenanceRecord record = new MaintenanceRecord();
            
            record.setRecordId(rs.getString("record_id"));
            record.setRegistrationNumber(rs.getString("registration_number"));
            record.setScheduledDate(rs.getTimestamp("scheduled_date").toLocalDateTime());
            
            // Parse enums from strings
            try {
                record.setType(MaintenanceRecord.MaintenanceType.valueOf(rs.getString("type")));
            } catch (IllegalArgumentException e) {
                // Default to ROUTINE if invalid type
                record.setType(MaintenanceRecord.MaintenanceType.ROUTINE);
            }
            
            try {
                record.setStatus(MaintenanceRecord.MaintenanceStatus.valueOf(rs.getString("status")));
            } catch (IllegalArgumentException e) {
                // Default to SCHEDULED if invalid status
                record.setStatus(MaintenanceRecord.MaintenanceStatus.SCHEDULED);
            }
            
            record.setTechnician(rs.getString("technician"));
            record.setDescription(rs.getString("description"));
            
            // Handle nullable fields
            Timestamp completionDateTimestamp = rs.getTimestamp("completion_date");
            if (completionDateTimestamp != null) {
                record.setCompletionDate(completionDateTimestamp.toLocalDateTime());
            }
            
            record.setNotes(rs.getString("notes"));
            
            return record;
        }
    }
}