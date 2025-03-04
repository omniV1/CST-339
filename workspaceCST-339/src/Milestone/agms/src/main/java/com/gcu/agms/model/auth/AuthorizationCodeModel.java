package com.gcu.agms.model.auth;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Model class representing authorization codes used for privileged role registration.
 * Authorization codes provide an added layer of security for admin and operations roles.
 * 
 * @author Airport Gate Management System
 * @version 4.0
 */
@Data
public class AuthorizationCodeModel {
    private Long id;
    private String code;
    private UserRole role;
    private Boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private Long usedBy;
    private LocalDateTime usedAt;
    private LocalDateTime expiresAt;
    
    /**
     * Checks if the authorization code is valid for use.
     * A code is valid if it's active and either has no expiration or hasn't expired yet.
     * 
     * @return true if the code is valid, false otherwise
     */
    public boolean isValid() {
        return isActive && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }
}