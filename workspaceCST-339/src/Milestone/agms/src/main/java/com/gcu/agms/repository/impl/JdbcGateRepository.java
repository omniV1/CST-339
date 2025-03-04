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
import org.springframework.stereotype.Repository;

import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.model.gate.GateStatus;
import com.gcu.agms.repository.GateRepository;

/**
 * JDBC implementation of the GateRepository interface.
 * This class handles data access operations for gates using Spring JDBC.
 */
@Repository
public class JdbcGateRepository implements GateRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcGateRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcGateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized JdbcGateRepository");
    }
    
    @Override
    public List<GateModel> findAll() {
        logger.debug("Finding all gates");
        String sql = "SELECT * FROM gate";
        
        try {
            return jdbcTemplate.query(sql, new GateRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all gates: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Optional<GateModel> findById(Long id) {
        logger.debug("Finding gate by ID: {}", id);
        String sql = "SELECT * FROM gate WHERE id = ?";
        
        try {
            List<GateModel> results = jdbcTemplate.query(sql, new GateRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No gate found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding gate by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<GateModel> findByGateId(String gateId) {
        logger.debug("Finding gate by gate ID: {}", gateId);
        String sql = "SELECT * FROM gate WHERE gate_id = ?";
        
        try {
            List<GateModel> results = jdbcTemplate.query(sql, new GateRowMapper(), gateId);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No gate found with gate ID: {}", gateId);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding gate by gate ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<GateModel> findByTerminal(String terminal) {
        logger.debug("Finding gates by terminal: {}", terminal);
        String sql = "SELECT * FROM gate WHERE terminal = ?";
        
        try {
            return jdbcTemplate.query(sql, new GateRowMapper(), terminal);
        } catch (DataAccessException e) {
            logger.error("Database error finding gates by terminal: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<GateModel> findByStatus(String status) {
        logger.debug("Finding gates by status: {}", status);
        String sql = "SELECT * FROM gate WHERE status = ?";
        
        try {
            return jdbcTemplate.query(sql, new GateRowMapper(), status);
        } catch (DataAccessException e) {
            logger.error("Database error finding gates by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public GateModel save(GateModel gate) {
        if (gate.getId() == null) {
            // Insert new gate
            return insertGate(gate);
        } else {
            // Update existing gate
            return updateGate(gate);
        }
    }
    
    private GateModel insertGate(GateModel gate) {
        logger.debug("Inserting new gate: {}", gate.getGateId());
        
        String sql = "INSERT INTO gate (gate_id, terminal, gate_number, gate_type, gate_size, " +
                     "status, is_active, has_jet_bridge, capacity, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, gate.getGateId());
                ps.setString(2, gate.getTerminal());
                ps.setString(3, gate.getGateNumber());
                ps.setString(4, gate.getGateType().toString());
                ps.setString(5, gate.getGateSize().toString());
                ps.setString(6, gate.getStatus().toString());
                ps.setBoolean(7, gate.getIsActive());
                ps.setBoolean(8, gate.isHasJetBridge());
                ps.setInt(9, gate.getCapacity());
                
                // Set timestamps
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(10, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(11, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                gate.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting gate: {}", e.getMessage(), e);
        }
        
        return gate;
    }
    
    private GateModel updateGate(GateModel gate) {
        logger.debug("Updating gate: {}", gate.getGateId());
        
        String sql = "UPDATE gate SET terminal = ?, gate_number = ?, gate_type = ?, " +
                     "gate_size = ?, status = ?, is_active = ?, has_jet_bridge = ?, " +
                     "capacity = ?, updated_at = ? WHERE id = ?";
        
        try {
            jdbcTemplate.update(
                sql,
                gate.getTerminal(),
                gate.getGateNumber(),
                gate.getGateType().toString(),
                gate.getGateSize().toString(),
                gate.getStatus().toString(),
                gate.getIsActive(),
                gate.isHasJetBridge(),
                gate.getCapacity(),
                Timestamp.valueOf(LocalDateTime.now()),
                gate.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Database error updating gate: {}", e.getMessage(), e);
        }
        
        return gate;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting gate with ID: {}", id);
        
        String sql = "DELETE FROM gate WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting gate: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsByGateId(String gateId) {
        logger.debug("Checking if gate exists with gateId: {}", gateId);
        String sql = "SELECT COUNT(*) FROM gate WHERE gate_id = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gateId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking if gate exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public int countByStatus(String status) {
        logger.debug("Counting gates by status: {}", status);
        String sql = "SELECT COUNT(*) FROM gate WHERE status = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting gates by status: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all gates");
        String sql = "SELECT COUNT(*) FROM gate";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting all gates: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Row mapper for converting database rows to GateModel objects.
     */
    private static class GateRowMapper implements RowMapper<GateModel> {
        @Override
        public GateModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            GateModel gate = new GateModel();
            gate.setId(rs.getLong("id"));
            gate.setGateId(rs.getString("gate_id"));
            gate.setTerminal(rs.getString("terminal"));
            gate.setGateNumber(rs.getString("gate_number"));
            
            // Parse enums from strings
            gate.setGateType(GateModel.GateType.valueOf(rs.getString("gate_type")));
            gate.setGateSize(GateModel.GateSize.valueOf(rs.getString("gate_size")));
            gate.setStatus(GateStatus.valueOf(rs.getString("status")));
            
            // Handle boolean fields
            gate.setIsActive(rs.getBoolean("is_active"));
            gate.setHasJetBridge(rs.getBoolean("has_jet_bridge"));
            
            // Handle numeric fields
            gate.setCapacity(rs.getInt("capacity"));
            
            return gate;
        }
    }
}