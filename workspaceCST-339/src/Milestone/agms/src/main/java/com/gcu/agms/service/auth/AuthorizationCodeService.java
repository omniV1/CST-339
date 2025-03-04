package com.gcu.agms.service.auth;

import java.time.LocalDateTime;
import java.util.List;

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

/**
 * Service interface for authorization code management.
 * Defines methods for validating, generating, and managing authorization codes.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
public interface AuthorizationCodeService {
    /**
     * Validates whether a given authorization code grants access to a specific role.
     * This method ensures that users can only register for roles they are authorized to hold.
     * 
     * @param authCode The authorization code provided during registration
     * @param requestedRole The role the user is attempting to register for
     * @return true if the code is valid for the requested role, false otherwise
     */
    boolean isValidAuthCode(String authCode, UserRole requestedRole);
    
    /**
     * Marks an authorization code as used by a specific user.
     * This method updates the code record to indicate which user used it and when.
     * 
     * @param authCode The authorization code to mark as used
     * @param user The user who used the code
     */
    default void markCodeAsUsed(String authCode, UserModel user) {
        // Default empty implementation for backward compatibility
    }
    
    /**
     * Generates a new authorization code for a specific role.
     * Creates a secure random code and stores it in the database.
     * 
     * @param role The role the code will authorize
     * @param description A description of the code's purpose
     * @param expiresAt When the code will expire (null for no expiration)
     * @return The generated code string
     */
    default String generateNewCode(UserRole role, String description, LocalDateTime expiresAt) {
        // Default empty implementation for backward compatibility
        return "";
    }
    
    /**
     * Retrieves all authorization codes in the system.
     * 
     * @return A list of all authorization codes
     */
    default List<AuthorizationCodeModel> getAllAuthCodes() {
        // Default empty implementation for backward compatibility
        return List.of();
    }
    
    /**
     * Deactivates an authorization code by ID.
     * Deactivated codes can no longer be used for registration.
     * 
     * @param id The ID of the code to deactivate
     * @return true if deactivation was successful, false if code wasn't found
     */
    default boolean deactivateAuthCode(Long id) {
        // Default empty implementation for backward compatibility
        return false;
    }
    
    /**
     * Deletes an authorization code from the system.
     * 
     * @param id The ID of the code to delete
     */
    default void deleteAuthCode(Long id) {
        // Default empty implementation for backward compatibility
    }
}