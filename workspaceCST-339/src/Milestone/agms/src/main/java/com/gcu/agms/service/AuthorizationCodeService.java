package com.gcu.agms.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.gcu.agms.model.UserRole;

/**
 * Service for handling authorization codes and validating them against user roles.
 * 
 * This service maintains a set of predefined authorization codes mapped to specific user roles.
 * It provides functionality to validate if a given authorization code is valid for a requested role.
 * 
 * <p>Note: In a real-world application, authorization codes should be stored securely in a database
 * rather than hardcoded in the application.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * AuthorizationCodeService authService = new AuthorizationCodeService();
 * boolean isValid = authService.isValidAuthCode("ADMIN2025", UserRole.ADMIN);
 * }
 * </pre>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>{@link java.util.Map}</li>
 *   <li>{@link java.util.HashMap}</li>
 *   <li>{@link com.gcu.agms.model.UserRole}</li>
 * </ul>
 * 
 * @see com.gcu.agms.model.UserRole
 */
@Service
public class AuthorizationCodeService {
    // We store our authorization codes in a Map where the key is the code and the value is the role it authorizes
    private final Map<String, UserRole> validAuthCodes;
    
    // Constructor initializes our authorization codes
    public AuthorizationCodeService() {
        validAuthCodes = new HashMap<>();
        
        // Here we define our authorization codes for different administrative roles
        // In a real application, these would be stored securely in a database
        validAuthCodes.put("ADMIN2025", UserRole.ADMIN);
        validAuthCodes.put("OPS2025", UserRole.OPERATIONS_MANAGER);
    }
    
    /**
     * Validates if the provided authorization code is valid for the requested role.
     * 
     * @param authCode The authorization code provided during registration
     * @param requestedRole The role the user is attempting to register for
     * @return true if the code is valid for the requested role, false otherwise
     */
    public boolean isValidAuthCode(String authCode, UserRole requestedRole) {
        // If no auth code was provided, it can't be valid
        if (authCode == null || authCode.trim().isEmpty()) {
            return false;
        }
        
        // Check if the auth code exists in our valid codes
        UserRole authorizedRole = validAuthCodes.get(authCode);
        
        // The code is valid if it exists and matches the requested role
        return authorizedRole != null && authorizedRole == requestedRole;
    }
}