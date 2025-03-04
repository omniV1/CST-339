package com.gcu.agms.repository;

import java.util.List;
import java.util.Optional;

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserRole;

/**
 * Repository interface for authorization code data access operations.
 * Defines methods for CRUD operations and specific queries related to authorization codes.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
public interface AuthorizationCodeRepository {
    
    /**
     * Find an authorization code by its code value.
     * 
     * @param code The code to search for
     * @return Optional containing the authorization code if found, empty otherwise
     */
    Optional<AuthorizationCodeModel> findByCode(String code);
    
    /**
     * Find an authorization code by its ID.
     * 
     * @param id The ID to search for
     * @return Optional containing the authorization code if found, empty otherwise
     */
    Optional<AuthorizationCodeModel> findById(Long id);
    
    /**
     * Save an authorization code to the database.
     * If the code has no ID, it will be inserted as a new record.
     * If it has an ID, the existing record will be updated.
     * 
     * @param code The code to save
     * @return The saved code with generated ID (for new records)
     */
    AuthorizationCodeModel save(AuthorizationCodeModel code);
    
    /**
     * Delete an authorization code by its ID.
     * 
     * @param id The ID of the code to delete
     */
    void deleteById(Long id);
    
    /**
     * Retrieve all authorization codes.
     * 
     * @return List of all authorization codes
     */
    List<AuthorizationCodeModel> findAll();
    
    /**
     * Find active authorization codes for a specific role.
     * Active codes are those that are marked as active and haven't expired.
     * 
     * @param role The role to filter by
     * @return List of active authorization codes for the role
     */
    List<AuthorizationCodeModel> findActiveCodesByRole(UserRole role);
}