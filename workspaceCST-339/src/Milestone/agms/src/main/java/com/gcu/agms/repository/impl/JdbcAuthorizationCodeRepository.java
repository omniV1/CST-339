package com.gcu.agms.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.repository.AuthorizationCodeRepository;

/**
 * JDBC implementation of the AuthorizationCodeRepository interface.
 * Provides data access methods for authorization codes using Spring JDBC.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
// Removed @Repository since we define this as a bean in the config class
public class JdbcAuthorizationCodeRepository implements AuthorizationCodeRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcAuthorizationCodeRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * 
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcAuthorizationCodeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Optional<AuthorizationCodeModel> findByCode(String code) {
        logger.debug("Finding authorization code: {}", code);
        String sql = "SELECT * FROM authorization_codes WHERE code = ?";
        
        try {
            List<AuthorizationCodeModel> results = jdbcTemplate.query(
                sql, 
                new AuthorizationCodeRowMapper(), 
                code
            );
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            // This is expected if no results are found
            logger.debug("No authorization code found with code: {}", code);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding authorization code: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<AuthorizationCodeModel> findById(Long id) {
        logger.debug("Finding authorization code by ID: {}", id);
        String sql = "SELECT * FROM authorization_codes WHERE id = ?";
        
        try {
            List<AuthorizationCodeModel> results = jdbcTemplate.query(
                sql, 
                new AuthorizationCodeRowMapper(), 
                id
            );
            
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No authorization code found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding authorization code by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public AuthorizationCodeModel save(AuthorizationCodeModel code) {
        if (code.getId() == null) {
            // Insert new code
            return insertCode(code);
        } else {
            // Update existing code
            return updateCode(code);
        }
    }
    
    /**
     * Insert a new authorization code record.
     * 
     * @param code The code to insert
     * @return The inserted code with generated ID
     */
    private AuthorizationCodeModel insertCode(AuthorizationCodeModel code) {
        logger.debug("Inserting new authorization code: {}", code.getCode());
        
        String sql = "INSERT INTO authorization_codes (code, role, is_active, description, created_at, used_by, used_at, expires_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, code.getCode());
                ps.setString(2, code.getRole().toString());
                ps.setBoolean(3, code.getIsActive());
                ps.setString(4, code.getDescription());
                ps.setObject(5, code.getCreatedAt());
                
                // Handle nullable fields
                if (code.getUsedBy() != null) {
                    ps.setLong(6, code.getUsedBy());
                } else {
                    ps.setNull(6, java.sql.Types.BIGINT);
                }
                
                ps.setObject(7, code.getUsedAt());
                ps.setObject(8, code.getExpiresAt());
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                code.setId(key.longValue());
            }
        } catch (DataAccessException e) {
            logger.error("Database error inserting authorization code: {}", e.getMessage(), e);
        }
        
        return code;
    }
    
    /**
     * Update an existing authorization code record.
     * 
     * @param code The code to update
     * @return The updated code
     */
    private AuthorizationCodeModel updateCode(AuthorizationCodeModel code) {
        logger.debug("Updating authorization code: {}", code.getCode());
        
        String sql = "UPDATE authorization_codes SET code = ?, role = ?, is_active = ?, " +
                     "description = ?, used_by = ?, used_at = ?, expires_at = ? WHERE id = ?";
        
        try {
            jdbcTemplate.update(
                sql, 
                code.getCode(),
                code.getRole().toString(),
                code.getIsActive(),
                code.getDescription(),
                code.getUsedBy(),
                code.getUsedAt(),
                code.getExpiresAt(),
                code.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Database error updating authorization code: {}", e.getMessage(), e);
        }
        
        return code;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting authorization code with ID: {}", id);
        
        String sql = "DELETE FROM authorization_codes WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting authorization code: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public List<AuthorizationCodeModel> findAll() {
        logger.debug("Finding all authorization codes");
        
        String sql = "SELECT * FROM authorization_codes ORDER BY created_at DESC";
        
        try {
            return jdbcTemplate.query(sql, new AuthorizationCodeRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all authorization codes: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<AuthorizationCodeModel> findActiveCodesByRole(UserRole role) {
        logger.debug("Finding active authorization codes for role: {}", role);
        
        String sql = "SELECT * FROM authorization_codes WHERE role = ? AND is_active = true " +
                     "AND (expires_at IS NULL OR expires_at > NOW())";
        
        try {
            return jdbcTemplate.query(sql, new AuthorizationCodeRowMapper(), role.toString());
        } catch (DataAccessException e) {
            logger.error("Database error finding active codes by role: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Row mapper for converting database rows to AuthorizationCodeModel objects.
     */
    private static class AuthorizationCodeRowMapper implements RowMapper<AuthorizationCodeModel> {
        @Override
        public AuthorizationCodeModel mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            AuthorizationCodeModel code = new AuthorizationCodeModel();
            code.setId(rs.getLong("id"));
            code.setCode(rs.getString("code"));
            code.setRole(UserRole.valueOf(rs.getString("role")));
            code.setIsActive(rs.getBoolean("is_active"));
            code.setDescription(rs.getString("description"));
            code.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            
            // Handle nullable columns
            Long usedBy = rs.getLong("used_by");
            if (rs.wasNull()) {
                usedBy = null;
            }
            code.setUsedBy(usedBy);
            
            code.setUsedAt(rs.getObject("used_at", LocalDateTime.class));
            code.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
            
            return code;
        }
    }
}