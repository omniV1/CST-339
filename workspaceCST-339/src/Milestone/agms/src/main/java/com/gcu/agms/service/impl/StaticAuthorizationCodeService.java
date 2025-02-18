package com.gcu.agms.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;

import jakarta.annotation.PostConstruct;

/**
 * Provides a static, in-memory implementation of authorization code validation.
 * This implementation maintains a fixed set of authorization codes in memory,
 * making it suitable for development and testing environments. In a production
 * environment, this would typically be replaced with a more secure implementation
 * that validates codes against a database or external authentication service.
 */
@Service
public class StaticAuthorizationCodeService implements AuthorizationCodeService {
    private static final Logger logger = LoggerFactory.getLogger(StaticAuthorizationCodeService.class);
    
    // Using a Map to store valid authorization codes and their corresponding roles
    private final Map<String, UserRole> validAuthCodes;

    /**
     * Initializes the service with a set of predefined authorization codes.
     * In a production environment, these codes would be stored securely and
     * potentially managed through an administrative interface.
     */
    public StaticAuthorizationCodeService() {
        // Create a temporary map for initialization
        Map<String, UserRole> codes = new HashMap<>();
        codes.put("ADMIN2025", UserRole.ADMIN);
        codes.put("OPS2025", UserRole.OPERATIONS_MANAGER);
        
        // Assign an unmodifiable view of the map to prevent external modification
        validAuthCodes = Collections.unmodifiableMap(codes);
        
        logger.info("Static authorization codes initialized for {} roles", 
                   validAuthCodes.size());
    }
    
    @PostConstruct
    public void init() {
        logger.info("Authorization code service started");
        logger.debug("Available roles with auth codes: {}", 
                    validAuthCodes.values());
    }
    
    @Override
    public boolean isValidAuthCode(String authCode, UserRole requestedRole) {
        logger.debug("Validating auth code for role: {}", requestedRole);
        
        if (authCode == null || authCode.trim().isEmpty()) {
            logger.warn("Empty or null authorization code provided");
            return false;
        }
        
        UserRole authorizedRole = validAuthCodes.get(authCode);
        boolean isValid = authorizedRole != null && authorizedRole == requestedRole;
        
        if (isValid) {
            logger.info("Valid authorization code provided for role: {}", requestedRole);
        } else {
            logger.warn("Invalid authorization code provided for role: {}", requestedRole);
        }
        
        return isValid;
    }
}