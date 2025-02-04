package com.gcu.agms.service;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.UserModel;

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
    boolean authenticate(String username, String password);
    
    /**
     * Retrieves all users in the system.
     * @return List of all registered users
     */
    List<UserModel> getAllUsers();
}