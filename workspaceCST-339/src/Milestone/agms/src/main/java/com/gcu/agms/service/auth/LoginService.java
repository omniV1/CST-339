package com.gcu.agms.service.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.LoginModel;
import com.gcu.agms.model.auth.UserModel;

/**
 * Service responsible for handling user authentication and login operations.
 * This service implements proper dependency injection and separation of concerns
 * as required by Milestone 3.
 */
@Service
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    
    private final UserService userService;
    
    // Constructor injection for UserService
    public LoginService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Authenticates a user based on login credentials.
     * This method separates authentication logic from the controller layer.
     * 
     * @param loginModel the login credentials to verify
     * @return Optional containing the authenticated user if successful, empty otherwise
     */
    public Optional<UserModel> authenticate(LoginModel loginModel) {
        logger.info("Attempting authentication for user: {}", loginModel.getUsername());
        
        // Find user by username. Password verification is now handled by Spring Security.
        return userService.findByUsername(loginModel.getUsername())
            // .filter(user -> userService.authenticate(loginModel.getUsername(), loginModel.getPassword())) // Removed: Authentication check handled by Spring Security
            .map(user -> {
                // Consider moving lastLogin update to a Spring Security success handler if needed
                user.setLastLogin(LocalDateTime.now()); 
                logger.info("User found: {}", loginModel.getUsername()); // Changed log message
                return user;
            });
    }

    /**
     * Validates the format of login credentials before attempting authentication.
     * 
     * @param loginModel the credentials to validate
     * @return true if credentials are in valid format
     */
    public boolean validateCredentials(LoginModel loginModel) {
        if (loginModel == null) {
            return false;
        }
        
        String username = loginModel.getUsername();
        String password = loginModel.getPassword();
        
        if (username == null || password == null) {
            return false;
        }
        
        username = username.trim();
        password = password.trim();
        
        return !username.isEmpty() && username.length() <= 50 &&
               !password.isEmpty() && password.length() <= 50;
    }
}