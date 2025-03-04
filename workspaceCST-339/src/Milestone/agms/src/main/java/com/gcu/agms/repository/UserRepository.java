package com.gcu.agms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.auth.UserModel;

/**
 * Repository interface for user data access operations.
 * Defines methods for performing CRUD operations and specific queries related to users.
 */
public interface UserRepository {
    
    /**
     * Find a user by their username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty Optional otherwise
     */
    Optional<UserModel> findByUsername(String username);
    
    /**
     * Find a user by their ID.
     * 
     * @param id The ID to search for
     * @return Optional containing the user if found, empty Optional otherwise
     */
    Optional<UserModel> findById(Long id);
    
    /**
     * Retrieve all users.
     * 
     * @return List of all users
     */
    List<UserModel> findAll();
    
    /**
     * Save a user to the database.
     * If the user has no ID, they will be inserted as a new record.
     * If they have an ID, the existing record will be updated.
     * 
     * @param user The user to save
     * @return The saved user with generated ID (for new records)
     */
    UserModel save(UserModel user);
    
    /**
     * Delete a user by their ID.
     * 
     * @param id The ID of the user to delete
     */
    void deleteById(Long id);
    
    /**
     * Check if a user exists with the given username.
     * 
     * @param username The username to check
     * @return true if a user exists with the username, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Update the last login timestamp for a user.
     * 
     * @param userId The ID of the user to update
     * @param lastLogin The new last login timestamp
     */
    void updateLastLogin(Long userId, LocalDateTime lastLogin);
}