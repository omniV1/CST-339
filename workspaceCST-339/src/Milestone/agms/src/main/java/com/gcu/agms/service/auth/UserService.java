package com.gcu.agms.service.auth;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.auth.UserModel;

/**
 * Defines the core user management operations for the AGMS system.
 * This interface represents the contract that any user service implementation
 * must fulfill, following the Interface Segregation Principle.
 */
public interface UserService {
    /**
     * Registers a new user in the system.
     * @param newUser The user model containing registration details
     * @return true if registration was successful, false if username already exists
     */
    boolean registerUser(UserModel newUser);
    
    /**
     * Finds a user by their username.
     * @param username The username to search for
     * @return Optional containing the user if found, empty Optional otherwise
     */
    Optional<UserModel> findByUsername(String username);
    
    /**
     * Authenticates a user based on username and password.
     * @param username The username to authenticate
     * @param password The password to verify
     * @return true if authentication successful, false otherwise
     */
    // boolean authenticate(String username, String password); // Removed - Handled by Spring Security
    
    /**
     * Retrieves all users in the system.
     * @return List of all registered users
     */
    List<UserModel> getAllUsers();
    
    /**
     * Deletes a user from the system by their ID.
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false if user not found
     */
    boolean deleteUser(Long id);
    
    /**
     * Finds a user by their ID.
     * @param id The ID of the user to find
     * @return The user if found, null otherwise
     */
    UserModel getUserById(Long id);
    
    /**
     * Updates an existing user's information.
     * @param userModel The updated user information
     * @return true if update was successful, false otherwise
     */
    boolean updateUser(UserModel userModel);
}