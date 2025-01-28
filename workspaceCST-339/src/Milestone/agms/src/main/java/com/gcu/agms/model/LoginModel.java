package com.gcu.agms.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Login model class for handling user authentication requests
 */
@Data
public class LoginModel {
    @NotEmpty(message = "Username is required")
    private String username;
    
    @NotEmpty(message = "Password is required")
    private String password;
}