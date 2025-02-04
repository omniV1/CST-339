package com.gcu.agms.service;

import com.gcu.agms.model.UserRole;

/**
 * Defines the contract for authorization code validation in the AGMS system.
 * This service is responsible for validating that users have the appropriate
 * authorization codes when registering for privileged roles like ADMIN or
 * OPERATIONS_MANAGER. The separation between interface and implementation allows
 * for different validation strategies to be used (e.g., static codes, database lookup,
 * external validation service).
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
}
