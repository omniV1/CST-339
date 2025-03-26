package com.gcu.agms.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.model.gate.GateStatus;
import com.gcu.agms.repository.GateRepository;

@Repository
public class JdbcGateRepository extends BaseJdbcRepository<GateModel, Long> implements GateRepository {
    
    public JdbcGateRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    
    @Override
    public List<GateModel> findAll() {
        logger.debug("Finding all gates with enhanced error handling");
        String sql = "SELECT * FROM gate ORDER BY terminal, gate_number";
        
        try {
            List<GateModel> gates = jdbcTemplate.query(sql, new GateRowMapper());
            logger.info("Successfully retrieved {} gates from database", gates.size());
            return gates;
        } catch (Exception e) {
            logger.error("Database error retrieving all gates: {}", e.getMessage(), e);
            return new ArrayList<>(); // Return empty list instead of throwing exception
        }
    }
    
    @Override
    public Optional<GateModel> findById(Long id) {
        logger.debug("Finding gate by ID: {}", id);
        String sql = "SELECT * FROM gate WHERE id = ?";
        return executeFindOne(sql, new GateRowMapper(), id);
    }
    
    @Override
    public Optional<GateModel> findByGateId(String gateId) {
        logger.debug("Finding gate by gate ID: {}", gateId);
        String sql = "SELECT * FROM gate WHERE gate_id = ?";
        return executeFindOne(sql, new GateRowMapper(), gateId);
    }
    
    @Override
    public List<GateModel> findByTerminal(String terminal) {
        logger.debug("Finding gates by terminal: {}", terminal);
        String sql = "SELECT * FROM gate WHERE terminal = ?";
        return executeQuery(sql, new GateRowMapper(), terminal);
    }
    
    @Override
    public List<GateModel> findByStatus(String status) {
        logger.debug("Finding gates by status: {}", status);
        String sql = "SELECT * FROM gate WHERE status = ?";
        return executeQuery(sql, new GateRowMapper(), status);
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
            
        } catch (Exception e) {
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
        } catch (Exception e) {
            logger.error("Database error updating gate: {}", e.getMessage(), e);
        }
        
        return gate;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting gate with ID: {}", id);
        String sql = "DELETE FROM gate WHERE id = ?";
        executeUpdate(sql, id);
    }
    
    @Override
    public boolean existsByGateId(String gateId) {
        logger.debug("Checking if gate exists with gateId: {}", gateId);
        String sql = "SELECT COUNT(*) FROM gate WHERE gate_id = ?";
        return executeExists(sql, gateId);
    }
    
    @Override
    public int countByStatus(String status) {
        logger.debug("Counting gates by status: {}", status);
        String sql = "SELECT COUNT(*) FROM gate WHERE status = ?";
        return executeCount(sql, status);
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all gates");
        String sql = "SELECT COUNT(*) FROM gate";
        return executeCount(sql);
    }
    
       /**
     * Row mapper for converting database rows to GateModel objects.
     * Enhanced with error handling for enum parsing.
     */
    private static class GateRowMapper implements RowMapper<GateModel> {
        @Override
        public GateModel mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            GateModel gate = new GateModel();
            
            try {
                gate.setId(rs.getLong("id"));
                gate.setGateId(rs.getString("gate_id"));
                gate.setTerminal(rs.getString("terminal"));
                gate.setGateNumber(rs.getString("gate_number"));
                
                // Parse enums with proper error handling
                try {
                    gate.setGateType(GateModel.GateType.valueOf(rs.getString("gate_type")));
                } catch (IllegalArgumentException e) {
                    // Default to DOMESTIC if the type is invalid
                    gate.setGateType(GateModel.GateType.DOMESTIC);
                }
                
                try {
                    gate.setGateSize(GateModel.GateSize.valueOf(rs.getString("gate_size")));
                } catch (IllegalArgumentException e) {
                    // Default to MEDIUM if the size is invalid
                    gate.setGateSize(GateModel.GateSize.MEDIUM);
                }
                
                try {
                    gate.setStatus(GateStatus.valueOf(rs.getString("status")));
                } catch (IllegalArgumentException e) {
                    // Default to AVAILABLE if the status is invalid
                    gate.setStatus(GateStatus.AVAILABLE);
                }
                
                // Handle boolean fields
                gate.setIsActive(rs.getBoolean("is_active"));
                gate.setHasJetBridge(rs.getBoolean("has_jet_bridge"));
                
                // Handle numeric fields
                gate.setCapacity(rs.getInt("capacity"));
                
                return gate;
            } catch (SQLException e) {
                // Log the error and re-throw
                System.err.println("Error mapping gate row: " + e.getMessage() + 
                                   " (Gate ID: " + rs.getString("gate_id") + ")");
                throw e;
            }
        }
    }
}