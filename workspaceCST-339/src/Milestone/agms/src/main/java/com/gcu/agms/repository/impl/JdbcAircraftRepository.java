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

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.AircraftType;
import com.gcu.agms.repository.AircraftRepository;

/**
 * JDBC implementation of the AircraftRepository interface.
 * This class handles data access operations for aircraft using Spring JDBC.
 */
@Repository
public class JdbcAircraftRepository implements AircraftRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAircraftRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcAircraftRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized JdbcAircraftRepository");
    }
    
    @Override
    public List<AircraftModel> findAll() {
        logger.debug("Finding all aircraft");
        String sql = "SELECT * FROM aircraft";
        
        try {
            return jdbcTemplate.query(sql, new AircraftRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all aircraft: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Optional<AircraftModel> findById(Long id) {
        logger.debug("Finding aircraft by ID: {}", id);
        String sql = "SELECT * FROM aircraft WHERE id = ?";
        
        try {
            List<AircraftModel> results = jdbcTemplate.query(sql, new AircraftRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No aircraft found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding aircraft by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<AircraftModel> findByRegistrationNumber(String registrationNumber) {
        logger.debug("Finding aircraft by registration number: {}", registrationNumber);
        String sql = "SELECT * FROM aircraft WHERE registration_number = ?";
        
        try {
            List<AircraftModel> results = jdbcTemplate.query(sql, new AircraftRowMapper(), registrationNumber);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No aircraft found with registration number: {}", registrationNumber);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding aircraft by registration number: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<AircraftModel> findByType(AircraftType type) {
        logger.debug("Finding aircraft by type: {}", type);
        String sql = "SELECT * FROM aircraft WHERE type = ?";
        
        try {
            return jdbcTemplate.query(sql, new AircraftRowMapper(), type.name());
        } catch (DataAccessException e) {
            logger.error("Database error finding aircraft by type: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AircraftModel> findByStatus(String status) {
        logger.debug("Finding aircraft by status: {}", status);
        String sql = "SELECT * FROM aircraft WHERE status = ?";
        
        try {
            return jdbcTemplate.query(sql, new AircraftRowMapper(), status);
        } catch (DataAccessException e) {
            logger.error("Database error finding aircraft by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AircraftModel> findAvailableAircraft() {
        logger.debug("Finding available aircraft");
        String sql = "SELECT * FROM aircraft WHERE status = 'AVAILABLE'";
        
        try {
            return jdbcTemplate.query(sql, new AircraftRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding available aircraft: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public AircraftModel save(AircraftModel aircraft) {
        if (aircraft.getId() == null) {
            // Insert new aircraft
            return insertAircraft(aircraft);
        } else {
            // Update existing aircraft
            return updateAircraft(aircraft);
        }
    }
    
    private AircraftModel insertAircraft(AircraftModel aircraft) {
        logger.debug("Inserting new aircraft: {}", aircraft.getRegistrationNumber());
        
        String sql = "INSERT INTO aircraft (registration_number, model, type, status, current_location, " +
                     "next_maintenance_due, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, aircraft.getRegistrationNumber());
                ps.setString(2, aircraft.getModel());
                ps.setString(3, aircraft.getType().name());
                ps.setString(4, aircraft.getStatus().name());
                ps.setString(5, aircraft.getCurrentLocation());
                
                // Handle nullable fields
                Timestamp maintenanceDue = aircraft.getNextMaintenanceDue() != null ? 
                    Timestamp.valueOf(aircraft.getNextMaintenanceDue()) : null;
                ps.setTimestamp(6, maintenanceDue);
                
                // Set timestamps
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(7, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(8, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                aircraft.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting aircraft: {}", e.getMessage(), e);
        }
        
        return aircraft;
    }
    
    private AircraftModel updateAircraft(AircraftModel aircraft) {
        logger.debug("Updating aircraft: {}", aircraft.getRegistrationNumber());
        
        String sql = "UPDATE aircraft SET model = ?, type = ?, status = ?, current_location = ?, " +
                     "next_maintenance_due = ?, updated_at = ? WHERE id = ?";
        
        try {
            jdbcTemplate.update(
                sql,
                aircraft.getModel(),
                aircraft.getType().name(),
                aircraft.getStatus().name(),
                aircraft.getCurrentLocation(),
                aircraft.getNextMaintenanceDue() != null ? 
                    Timestamp.valueOf(aircraft.getNextMaintenanceDue()) : null,
                Timestamp.valueOf(LocalDateTime.now()),
                aircraft.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Database error updating aircraft: {}", e.getMessage(), e);
        }
        
        return aircraft;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting aircraft with ID: {}", id);
        
        String sql = "DELETE FROM aircraft WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting aircraft: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateStatus(String registrationNumber, String status, String location) {
        logger.debug("Updating status for aircraft {}: {} at {}", registrationNumber, status, location);
        
        String sql = "UPDATE aircraft SET status = ?, current_location = ?, updated_at = ? " +
                     "WHERE registration_number = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(
                sql,
                status,
                location,
                Timestamp.valueOf(LocalDateTime.now()),
                registrationNumber
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error updating aircraft status: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateMaintenanceDate(String registrationNumber, LocalDateTime maintenanceDate) {
        logger.debug("Updating maintenance date for aircraft {}: {}", registrationNumber, maintenanceDate);
        
        String sql = "UPDATE aircraft SET next_maintenance_due = ?, updated_at = ? " +
                     "WHERE registration_number = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(
                sql,
                maintenanceDate != null ? Timestamp.valueOf(maintenanceDate) : null,
                Timestamp.valueOf(LocalDateTime.now()),
                registrationNumber
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error updating maintenance date: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public int countByStatus(String status) {
        logger.debug("Counting aircraft by status: {}", status);
        String sql = "SELECT COUNT(*) FROM aircraft WHERE status = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting aircraft by status: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all aircraft");
        String sql = "SELECT COUNT(*) FROM aircraft";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting all aircraft: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public boolean existsByRegistrationNumber(String registrationNumber) {
        logger.debug("Checking if aircraft exists with registration number: {}", registrationNumber);
        String sql = "SELECT COUNT(*) FROM aircraft WHERE registration_number = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, registrationNumber);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking if aircraft exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Row mapper for converting database rows to AircraftModel objects.
     */
    private static class AircraftRowMapper implements RowMapper<AircraftModel> {
        @Override
        public AircraftModel mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            AircraftModel aircraft = new AircraftModel();
            aircraft.setId(rs.getLong("id"));
            aircraft.setRegistrationNumber(rs.getString("registration_number"));
            aircraft.setModel(rs.getString("model"));
            
            // Parse enums from strings
            try {
                aircraft.setType(AircraftType.valueOf(rs.getString("type")));
            } catch (IllegalArgumentException e) {
                // Default to NARROW_BODY if invalid type
                aircraft.setType(AircraftType.NARROW_BODY);
            }
            
            try {
                aircraft.setStatus(AircraftModel.AircraftStatus.valueOf(rs.getString("status")));
            } catch (IllegalArgumentException e) {
                // Default to AVAILABLE if invalid status
                aircraft.setStatus(AircraftModel.AircraftStatus.AVAILABLE);
            }
            
            aircraft.setCurrentLocation(rs.getString("current_location"));
            
            // Handle nullable fields
            Timestamp maintenanceDueTimestamp = rs.getTimestamp("next_maintenance_due");
            if (maintenanceDueTimestamp != null) {
                aircraft.setNextMaintenanceDue(maintenanceDueTimestamp.toLocalDateTime());
            }
            
            return aircraft;
        }
    }
}