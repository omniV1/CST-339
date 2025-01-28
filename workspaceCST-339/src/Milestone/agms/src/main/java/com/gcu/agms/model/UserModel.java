package com.gcu.agms.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User model class that represents a user in the system
 * Contains all necessary fields for user registration and validation
 */
@Data
public class UserModel {
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
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Please provide a valid phone number")
    private String phoneNumber;
    
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;
    
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotNull(message = "User role is required")
    private UserRole role;
}
