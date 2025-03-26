package com.gcu.agms.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;
import com.gcu.agms.repository.AssignmentRepository;

/**
 * JDBC implementation of the AssignmentRepository interface.
 * This class handles data access operations for gate assignments using Spring JDBC.
 */
@Repository
public class JdbcAssignmentRepository implements AssignmentRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAssignmentRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcAssignmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized JdbcAssignmentRepository");
    }
    
    @Override
    public List<AssignmentModel> findAll() {
        logger.debug("Finding all assignments");
        String sql = "SELECT * FROM assignment ORDER BY start_time";
        
        try {
            return jdbcTemplate.query(sql, new AssignmentRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all assignments: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Optional<AssignmentModel> findById(Long id) {
        logger.debug("Finding assignment by ID: {}", id);
        String sql = "SELECT * FROM assignment WHERE id = ?";
        
        try {
            List<AssignmentModel> results = jdbcTemplate.query(sql, new AssignmentRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No assignment found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding assignment by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<AssignmentModel> findByGateId(String gateId) {
        logger.debug("Finding assignments for gate: {}", gateId);
        String sql = "SELECT * FROM assignment WHERE gate_id = ? ORDER BY start_time";
        
        try {
            return jdbcTemplate.query(sql, new AssignmentRowMapper(), gateId);
        } catch (DataAccessException e) {
            logger.error("Database error finding assignments by gate: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AssignmentModel> findByFlightNumber(String flightNumber) {
        logger.debug("Finding assignments for flight: {}", flightNumber);
        String sql = "SELECT * FROM assignment WHERE flight_number = ? ORDER BY start_time";
        
        try {
            return jdbcTemplate.query(sql, new AssignmentRowMapper(), flightNumber);
        } catch (DataAccessException e) {
            logger.error("Database error finding assignments by flight: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AssignmentModel> findByStatus(AssignmentStatus status) {
        logger.debug("Finding assignments by status: {}", status);
        String sql = "SELECT * FROM assignment WHERE status = ? ORDER BY start_time";
        
        try {
            return jdbcTemplate.query(sql, new AssignmentRowMapper(), status.name());
        } catch (DataAccessException e) {
            logger.error("Database error finding assignments by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AssignmentModel> findActiveAssignments() {
        logger.debug("Finding active assignments");
        String sql = "SELECT * FROM assignment WHERE ? BETWEEN start_time AND end_time " +
                     "AND is_cancelled = 0 ORDER BY start_time";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            return jdbcTemplate.query(sql, new AssignmentRowMapper(), Timestamp.valueOf(now));
        } catch (DataAccessException e) {
            logger.error("Database error finding active assignments: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AssignmentModel> findUpcomingAssignments() {
        logger.debug("Finding upcoming assignments");
        String sql = "SELECT * FROM assignment WHERE start_time > ? " +
                     "AND is_cancelled = 0 ORDER BY start_time";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            return jdbcTemplate.query(sql, new AssignmentRowMapper(), Timestamp.valueOf(now));
        } catch (DataAccessException e) {
            logger.error("Database error finding upcoming assignments: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Map<String, AssignmentModel> getCurrentAndNextAssignments(String gateId) {
        logger.debug("Getting current and next assignments for gate: {}", gateId);
        Map<String, AssignmentModel> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Find current assignment
        String currentSql = "SELECT * FROM assignment WHERE gate_id = ? " +
                           "AND ? BETWEEN start_time AND end_time " +
                           "AND is_cancelled = 0 LIMIT 1";
        
        // Find next assignment
        String nextSql = "SELECT * FROM assignment WHERE gate_id = ? " +
                        "AND start_time > ? " +
                        "AND is_cancelled = 0 " +
                        "ORDER BY start_time LIMIT 1";
        
        try {
            List<AssignmentModel> currentAssignments = jdbcTemplate.query(
                currentSql, 
                new AssignmentRowMapper(), 
                gateId, 
                Timestamp.valueOf(now)
            );
            
            if (!currentAssignments.isEmpty()) {
                result.put("current", currentAssignments.get(0));
            }
            
            List<AssignmentModel> nextAssignments = jdbcTemplate.query(
                nextSql, 
                new AssignmentRowMapper(), 
                gateId, 
                Timestamp.valueOf(now)
            );
            
            if (!nextAssignments.isEmpty()) {
                result.put("next", nextAssignments.get(0));
            }
        } catch (DataAccessException e) {
            logger.error("Database error getting current and next assignments: {}", e.getMessage(), e);
        }
        
        return result;
    }
    
    @Override
    public AssignmentModel save(AssignmentModel assignment) {
        if (assignment.getId() == null) {
            // Insert new assignment
            return insertAssignment(assignment);
        } else {
            // Update existing assignment
            return updateAssignment(assignment);
        }
    }
    
    private AssignmentModel insertAssignment(AssignmentModel assignment) {
        logger.debug("Inserting new assignment for gate: {}", assignment.getGateId());
        
        String sql = "INSERT INTO assignment (gate_id, flight_number, start_time, end_time, " +
                     "status, assigned_by, created_by, created_at, updated_at, is_cancelled) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, assignment.getGateId());
                ps.setString(2, assignment.getFlightNumber());
                ps.setTimestamp(3, Timestamp.valueOf(assignment.getStartTime()));
                ps.setTimestamp(4, Timestamp.valueOf(assignment.getEndTime()));
                ps.setString(5, assignment.getStatus().name());
                ps.setString(6, assignment.getAssignedBy());
                ps.setString(7, assignment.getCreatedBy());
                
                // Set timestamps
                ps.setTimestamp(8, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(9, Timestamp.valueOf(now)); // updated_at
                
                ps.setBoolean(10, assignment.isCancelled());
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                assignment.setId(key.longValue());
            }
            
            // Set created/updated timestamps
            assignment.setCreatedAt(now);
            assignment.setUpdatedAt(now);
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting assignment: {}", e.getMessage(), e);
        }
        
        return assignment;
    }
    
    private AssignmentModel updateAssignment(AssignmentModel assignment) {
        logger.debug("Updating assignment: {}", assignment.getId());
        
        String sql = "UPDATE assignment SET gate_id = ?, flight_number = ?, start_time = ?, " +
                     "end_time = ?, status = ?, assigned_by = ?, updated_at = ?, is_cancelled = ? " +
                     "WHERE id = ?";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            jdbcTemplate.update(
                sql,
                assignment.getGateId(),
                assignment.getFlightNumber(),
                Timestamp.valueOf(assignment.getStartTime()),
                Timestamp.valueOf(assignment.getEndTime()),
                assignment.getStatus().name(),
                assignment.getAssignedBy(),
                Timestamp.valueOf(now),
                assignment.isCancelled(),
                assignment.getId()
            );
            
            // Update updatedAt timestamp
            assignment.setUpdatedAt(now);
            
        } catch (DataAccessException e) {
            logger.error("Database error updating assignment: {}", e.getMessage(), e);
        }
        
        return assignment;
    }
    
    @Override
    public boolean deleteById(Long id) {
        logger.debug("Deleting assignment with ID: {}", id);
        
        String sql = "DELETE FROM assignment WHERE id = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error deleting assignment: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateStatus(Long id, AssignmentStatus status) {
        logger.debug("Updating status for assignment: {} to {}", id, status);
        
        String sql = "UPDATE assignment SET status = ?, updated_at = ? WHERE id = ?";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            int rowsAffected = jdbcTemplate.update(
                sql,
                status.name(),
                Timestamp.valueOf(now),
                id
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error updating assignment status: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean cancelAssignment(Long id) {
        logger.debug("Cancelling assignment with ID: {}", id);
        
        String sql = "UPDATE assignment SET is_cancelled = ?, status = ?, updated_at = ? WHERE id = ?";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            int rowsAffected = jdbcTemplate.update(
                sql,
                true,
                AssignmentStatus.CANCELLED.name(),
                Timestamp.valueOf(now),
                id
            );
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error cancelling assignment: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean hasConflict(AssignmentModel assignment) {
        logger.debug("Checking for conflicts with assignment for gate {}", assignment.getGateId());
        
        // SQL to find conflicts - assignments at the same gate with overlapping times
        String sql = "SELECT COUNT(*) FROM assignment WHERE gate_id = ? " +
                    "AND ((start_time <= ? AND end_time >= ?) OR (start_time <= ? AND end_time >= ?)) " +
                    "AND is_cancelled = 0";
        
        if (assignment.getId() != null) {
            // Exclude the current assignment when checking for conflicts (for updates)
            sql += " AND id != ?";
        }
        
        try {
            Integer count;
            if (assignment.getId() != null) {
                count = jdbcTemplate.queryForObject(
                    sql,
                    Integer.class,
                    assignment.getGateId(),
                    Timestamp.valueOf(assignment.getEndTime()),
                    Timestamp.valueOf(assignment.getStartTime()),
                    Timestamp.valueOf(assignment.getStartTime()),
                    Timestamp.valueOf(assignment.getEndTime()),
                    assignment.getId()
                );
            } else {
                count = jdbcTemplate.queryForObject(
                    sql,
                    Integer.class,
                    assignment.getGateId(),
                    Timestamp.valueOf(assignment.getEndTime()),
                    Timestamp.valueOf(assignment.getStartTime()),
                    Timestamp.valueOf(assignment.getStartTime()),
                    Timestamp.valueOf(assignment.getEndTime())
                );
            }
            
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking for conflicts: {}", e.getMessage(), e);
            return true; // Assume conflict if there's an error (safer approach)
        }
    }
    
    @Override
    public int countByStatus(AssignmentStatus status) {
        logger.debug("Counting assignments by status: {}", status);
        String sql = "SELECT COUNT(*) FROM assignment WHERE status = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status.name());
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting assignments by status: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countByGateId(String gateId) {
        logger.debug("Counting assignments for gate: {}", gateId);
        String sql = "SELECT COUNT(*) FROM assignment WHERE gate_id = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gateId);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting assignments by gate: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all assignments");
        String sql = "SELECT COUNT(*) FROM assignment";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting all assignments: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Row mapper for converting database rows to AssignmentModel objects.
     */
    private static class AssignmentRowMapper implements RowMapper<AssignmentModel> {
        @Override
        public AssignmentModel mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            AssignmentModel assignment = new AssignmentModel();
            assignment.setId(rs.getLong("id"));
            assignment.setGateId(rs.getString("gate_id"));
            assignment.setFlightNumber(rs.getString("flight_number"));
            assignment.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            assignment.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            
            // Parse enum from string
            try {
                assignment.setStatus(AssignmentStatus.valueOf(rs.getString("status")));
            } catch (IllegalArgumentException e) {
                // Default if enum value is invalid
                assignment.setStatus(AssignmentStatus.SCHEDULED);
            }
            
            assignment.setAssignedBy(rs.getString("assigned_by"));
            assignment.setCreatedBy(rs.getString("created_by"));
            
            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            if (createdAtTimestamp != null) {
                assignment.setCreatedAt(createdAtTimestamp.toLocalDateTime());
            }
            
            Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
            if (updatedAtTimestamp != null) {
                assignment.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
            }
            
            assignment.setCancelled(rs.getBoolean("is_cancelled"));
            
            return assignment;
        }
    }
}