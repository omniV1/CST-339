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

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.repository.UserRepository;

/**
 * JDBC implementation of the UserRepository interface.
 * This class handles data access operations for users using Spring JDBC.
 */
@Repository
public class JdbcUserRepository implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Optional<UserModel> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try {
            List<UserModel> results = jdbcTemplate.query(sql, new UserRowMapper(), username);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No user found with username: {}", username);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding user by username: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<UserModel> findAll() {
        logger.debug("Finding all users");
        String sql = "SELECT * FROM users";
        
        try {
            return jdbcTemplate.query(sql, new UserRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all users: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public UserModel save(UserModel user) {
        if (user.getId() == null) {
            // Insert new user
            return insertUser(user);
        } else {
            // Update existing user
            return updateUser(user);
        }
    }
    
    private UserModel insertUser(UserModel user) {
        logger.debug("Inserting new user: {}", user.getUsername());
        
        String sql = "INSERT INTO users (username, password, email, first_name, last_name, phone_number, role, " +
                     "is_active, is_enabled, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getFirstName());
                ps.setString(5, user.getLastName());
                ps.setString(6, user.getPhoneNumber());
                ps.setString(7, user.getRole().name());
                ps.setBoolean(8, user.isActive() != null ? user.isActive() : true);
                ps.setBoolean(9, user.isEnabled());
                
                // Set timestamps
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(10, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(11, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                user.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting user: {}", e.getMessage(), e);
        }
        
        return user;
    }
    
    private UserModel updateUser(UserModel user) {
        logger.debug("Updating user: {}", user.getUsername());
        
        String sql = "UPDATE users SET password = ?, email = ?, first_name = ?, last_name = ?, " +
                     "phone_number = ?, role = ?, is_active = ?, is_enabled = ?, updated_at = ? " +
                     "WHERE id = ?";
        
        try {
            jdbcTemplate.update(
                sql,
                user.getPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                user.isActive(),
                user.isEnabled(),
                Timestamp.valueOf(LocalDateTime.now()),
                user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Database error updating user: {}", e.getMessage(), e);
        }
        
        return user;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        String sql = "DELETE FROM users WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting user: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<UserModel> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try {
            List<UserModel> results = jdbcTemplate.query(sql, new UserRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No user found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding user by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Checking if user exists with username: {}", username);
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking if user exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public void updateLastLogin(Long userId, LocalDateTime lastLogin) {
        logger.debug("Updating last login for user ID: {}", userId);
        String sql = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, Timestamp.valueOf(lastLogin), userId);
        } catch (DataAccessException e) {
            logger.error("Database error updating last login: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Row mapper for converting database rows to UserModel objects.
     */
    private static class UserRowMapper implements RowMapper<UserModel> {
        @Override
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserModel user = new UserModel();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhoneNumber(rs.getString("phone_number"));
            
            // Parse role from string to enum
            user.setRole(UserRole.valueOf(rs.getString("role")));
            
            // Handle boolean fields
            user.setActive(rs.getBoolean("is_active"));
            user.setEnabled(rs.getBoolean("is_enabled"));
            
            // Handle timestamps
            Timestamp lastLoginTimestamp = rs.getTimestamp("last_login");
            user.setLastLogin(lastLoginTimestamp != null ? lastLoginTimestamp.toLocalDateTime() : null);
            
            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            user.setCreatedAt(createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null);
            
            Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
            user.setUpdatedAt(updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : null);
            
            return user;
        }
    }
}