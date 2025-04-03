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
 * 
 * This class is responsible for all database operations related to user entities, including:
 * - Creating new users (registration)
 * - Retrieving user information by ID or username
 * - Updating user information
 * - Deleting users
 * - Tracking login activity
 * 
 * It uses Spring's JdbcTemplate to interact with the database, which provides:
 * - SQL injection protection through prepared statements
 * - Connection pooling for performance
 * - Exception translation from JDBC exceptions to Spring's DataAccessException hierarchy
 * 
 * The Repository annotation marks this class as a Spring Data Access Object,
 * allowing it to be automatically discovered and injected where needed.
 */
@Repository
public class JdbcUserRepository implements UserRepository {

    /**
     * Logger for recording operation details and errors.
     * Uses SLF4J facade for flexible logging implementation.
     */
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);
    
    /**
     * JdbcTemplate provides the core JDBC operations.
     * This is injected by Spring and simplifies database interaction.
     */
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * Spring automatically provides a configured JdbcTemplate instance.
     * 
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Find a user by their username.
     * This is commonly used during authentication to verify credentials.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty Optional otherwise
     */
    @Override
    public Optional<UserModel> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        
        // SQL query to find a user by username
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try {
            // Execute query and map results to UserModel objects
            List<UserModel> results = jdbcTemplate.query(sql, new UserRowMapper(), username);
            
            // Return first result if any, otherwise empty Optional
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            // No results found (specific Spring exception)
            logger.debug("No user found with username: {}", username);
            return Optional.empty();
        } catch (DataAccessException e) {
            // Handle other database errors
            logger.error("Database error finding user by username: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Retrieve all users from the database.
     * Typically used for administrative functions to list all users.
     * 
     * @return List of all users, empty list if none found or in case of errors
     */
    @Override
    public List<UserModel> findAll() {
        logger.debug("Finding all users");
        
        // SQL query to select all users
        String sql = "SELECT * FROM users";
        
        try {
            // Execute query and map results to list of UserModel objects
            return jdbcTemplate.query(sql, new UserRowMapper());
        } catch (DataAccessException e) {
            // Handle database errors and return empty list
            logger.error("Database error finding all users: {}", e.getMessage(), e);
            return List.of(); // Return empty immutable list
        }
    }
    
    /**
     * Save a user to the database.
     * This method handles both insert (new user) and update (existing user) operations.
     * 
     * @param user The user model to save
     * @return The saved user with ID (set if it was a new user)
     */
    @Override
    public UserModel save(UserModel user) {
        // Check if user has an ID to determine if this is an insert or update
        if (user.getId() == null) {
            // Insert new user
            return insertUser(user);
        } else {
            // Update existing user
            return updateUser(user);
        }
    }
    
    /**
     * Insert a new user into the database.
     * This is a private helper method called by save() for new users.
     * 
     * @param user The new user to insert
     * @return The user with generated ID set
     */
    private UserModel insertUser(UserModel user) {
        logger.debug("Inserting new user: {}", user.getUsername());
        
        // SQL insert statement with placeholders for all user fields
        String sql = "INSERT INTO users (username, password, email, first_name, last_name, phone_number, role, " +
                     "is_active, is_enabled, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // KeyHolder will capture the auto-generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            // Execute update with prepared statement
            jdbcTemplate.update(connection -> {
                // Create prepared statement with instruction to return generated keys
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                
                // Set parameter values in order matching the SQL statement
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getFirstName());
                ps.setString(5, user.getLastName());
                ps.setString(6, user.getPhoneNumber());
                ps.setString(7, user.getRole().name());
                ps.setBoolean(8, user.isActive() != null ? user.isActive() : true);
                ps.setBoolean(9, user.isEnabled());
                
                // Set timestamps for audit fields
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(10, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(11, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            // Retrieve and set the generated ID
            Number key = keyHolder.getKey();
            if (key != null) {
                user.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            // Log error but don't rethrow to maintain service layer simplicity
            logger.error("Database error inserting user: {}", e.getMessage(), e);
        }
        
        return user;
    }
    
    /**
     * Update an existing user in the database.
     * This is a private helper method called by save() for existing users.
     * 
     * @param user The user with updated information
     * @return The updated user object
     */
    private UserModel updateUser(UserModel user) {
        logger.debug("Updating user: {}", user.getUsername());
        
        // SQL update statement for all mutable user fields
        String sql = "UPDATE users SET password = ?, email = ?, first_name = ?, last_name = ?, " +
                     "phone_number = ?, role = ?, is_active = ?, is_enabled = ?, updated_at = ? " +
                     "WHERE id = ?";
        
        try {
            // Execute update with all parameters
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
                Timestamp.valueOf(LocalDateTime.now()), // Set updated_at to current time
                user.getId()                            // WHERE clause condition
            );
        } catch (DataAccessException e) {
            // Log error but don't rethrow
            logger.error("Database error updating user: {}", e.getMessage(), e);
        }
        
        return user;
    }
    
    /**
     * Delete a user by their ID.
     * This permanently removes the user from the database.
     * 
     * @param id The ID of the user to delete
     */
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        // SQL delete statement
        String sql = "DELETE FROM users WHERE id = ?";
        
        try {
            // Execute delete operation
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            // Log error but don't rethrow
            logger.error("Database error deleting user: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Find a user by their ID.
     * This is typically used for retrieving user details for profile viewing or editing.
     * 
     * @param id The ID of the user to find
     * @return Optional containing the user if found, empty Optional otherwise
     */
    @Override
    public Optional<UserModel> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        
        // SQL query to find user by ID
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try {
            // Execute query and map results
            List<UserModel> results = jdbcTemplate.query(sql, new UserRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            // No results found
            logger.debug("No user found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            // Handle other database errors
            logger.error("Database error finding user by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Check if a user with the given username exists.
     * Used during registration to prevent duplicate usernames.
     * 
     * @param username The username to check
     * @return true if a user exists with the username, false otherwise
     */
    @Override
    public boolean existsByUsername(String username) {
        logger.debug("Checking if user exists with username: {}", username);
        
        // SQL query to count users with the given username
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try {
            // Execute count query
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            // Handle database errors
            logger.error("Database error checking if user exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Update the last login timestamp for a user.
     * Called when a user successfully logs in to track login activity.
     * 
     * @param userId The ID of the user
     * @param lastLogin The login timestamp
     */
    @Override
    public void updateLastLogin(Long userId, LocalDateTime lastLogin) {
        logger.debug("Updating last login for user ID: {}", userId);
        
        // SQL update statement for last_login field
        String sql = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try {
            // Execute update
            jdbcTemplate.update(sql, Timestamp.valueOf(lastLogin), userId);
        } catch (DataAccessException e) {
            // Log error but don't rethrow
            logger.error("Database error updating last login: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Row mapper for converting database rows to UserModel objects.
     * 
     * This inner class implements Spring's RowMapper interface to translate
     * database result set rows into domain objects. Each row from the users
     * table is mapped to a UserModel instance with all fields populated.
     */
    private static class UserRowMapper implements RowMapper<UserModel> {
        /**
         * Maps a single row of the result set to a UserModel object.
         * Called by JdbcTemplate for each row in the result set.
         * 
         * @param rs The result set containing database row data
         * @param rowNum The current row number (0-based)
         * @return A fully populated UserModel object
         * @throws SQLException If there is an error accessing the result set
         */
        @Override
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Create new UserModel instance
            UserModel user = new UserModel();
            
            // Map basic fields from result set to model fields
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhoneNumber(rs.getString("phone_number"));
            
            // Convert string role to enum type
            user.setRole(UserRole.valueOf(rs.getString("role")));
            
            // Map boolean fields
            user.setActive(rs.getBoolean("is_active"));
            user.setEnabled(rs.getBoolean("is_enabled"));
            
            // Map timestamp fields with null handling
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