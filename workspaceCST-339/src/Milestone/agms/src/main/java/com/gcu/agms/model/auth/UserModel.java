package com.gcu.agms.model.auth;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Enhanced UserModel that combines practical validation with domain model relationships.
 * This model bridges the gap between database requirements, security needs, and
 * user experience considerations. It maintains strong data validation while supporting
 * system auditing and tracking capabilities required by the domain model.
 * 
 * Key Features:
 * 1. Database Integration: Includes ID and audit fields for proper persistence
 * 2. Security: Strong validation rules for all user input
 * 3. Profile Management: Comprehensive user information fields
 * 4. Role Management: Support for authorization and access control
 * 5. Tracking: Audit fields for system monitoring
 * 
 * This model serves as a central entity in the system, having relationships with:
 * - Assignments (One-to-Many): Users create gate assignments
 * - AuditLog (One-to-Many): User actions are tracked in the audit log
 * - Notification (One-to-Many): Users receive system notifications
 */
@Data
public class UserModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Database and audit fields - Required by UML for entity tracking
    private Long id;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Authorization and role management
    private String authCode;
    
    
    @NotNull(message = "User role is required")
    private UserRole role = UserRole.PUBLIC;

    private boolean enabled;

    // User authentication fields with validation
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    // Personal information fields with validation
    @NotEmpty(message = "First name is required")
    @Size(min = 2, max = 32, message = "First name must be between 2 and 32 characters")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Size(min = 2, max = 32, message = "Last name must be between 2 and 32 characters")
    private String lastName;

    @NotEmpty(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Please provide a valid phone number")
    private String phoneNumber;

    // User message/notification field
    private String message;

    // UML-specified methods
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    // Additional getters and setters for enhanced functionality
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}