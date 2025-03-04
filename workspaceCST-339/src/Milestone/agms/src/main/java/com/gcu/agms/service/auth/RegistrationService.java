package com.gcu.agms.service.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

/**
 * Service responsible for handling user registration operations.
 * This service validates authorization codes for privileged roles and
 * manages the user registration process.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
@Service
public class RegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    
    private final UserService userService;
    private final AuthorizationCodeService authCodeService;
    
    /**
     * Constructor injection of required services.
     * 
     * @param userService Service for user management
     * @param authCodeService Service for authorization code validation
     */
    public RegistrationService(UserService userService, 
                             AuthorizationCodeService authCodeService) {
        this.userService = userService;
        this.authCodeService = authCodeService;
    }
    
    /**
     * Processes a new user registration with role verification.
     * For privileged roles (ADMIN, OPERATIONS_MANAGER), validates the provided
     * authorization code before allowing registration.
     * 
     * @param userModel The user information to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(UserModel userModel) {
        logger.info("Processing registration for user: {}", userModel.getUsername());
        
        // Validate role and authorization code if required
        if (requiresAuthCode(userModel.getRole()) && 
            !authCodeService.isValidAuthCode(userModel.getAuthCode(), userModel.getRole())) {
            logger.warn("Invalid authorization code for role: {}", userModel.getRole());
            return false;
        }
        
        // Set initial user properties
        userModel.setActive(true);
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdatedAt(LocalDateTime.now());
        
        // Attempt to register the user
        boolean registered = userService.registerUser(userModel);
        
        if (registered) {
            logger.info("Successfully registered user: {}", userModel.getUsername());
            
            // If auth code was used, mark it as used
            if (requiresAuthCode(userModel.getRole()) && userModel.getAuthCode() != null) {
                authCodeService.markCodeAsUsed(userModel.getAuthCode(), userModel);
                logger.info("Authorization code marked as used for user: {}", userModel.getUsername());
            }
        } else {
            logger.warn("Failed to register user: {}", userModel.getUsername());
        }
        
        return registered;
    }
    
    /**
     * Determines if a role requires an authorization code.
     * In AGMS, administrative roles require authorization codes for security.
     * 
     * @param role The role to check
     * @return true if the role requires an authorization code, false otherwise
     */
    private boolean requiresAuthCode(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.OPERATIONS_MANAGER;
    }
    
    /**
     * Validates the username is not already taken.
     * 
     * @param username The username to check
     * @return true if the username is available
     */
    public boolean isUsernameAvailable(String username) {
        return userService.findByUsername(username).isEmpty();
    }
}