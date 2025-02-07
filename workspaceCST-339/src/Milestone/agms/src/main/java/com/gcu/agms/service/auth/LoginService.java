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
        
        // Find user and verify credentials
        Optional<UserModel> userOpt = userService.findByUsername(loginModel.getUsername());
        
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            
            if (userService.authenticate(loginModel.getUsername(), loginModel.getPassword())) {
                // Update last login time
                user.setLastLogin(LocalDateTime.now());
                logger.info("Authentication successful for user: {}", loginModel.getUsername());
                return Optional.of(user);
            }
        }
        
        logger.warn("Authentication failed for user: {}", loginModel.getUsername());
        return Optional.empty();
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
        
        boolean isValid = loginModel.getUsername() != null 
                         && !loginModel.getUsername().trim().isEmpty()
                         && loginModel.getPassword() != null 
                         && !loginModel.getPassword().trim().isEmpty();
        
        if (!isValid) {
            logger.warn("Invalid login credential format");
        }
        
        return isValid;
    }
}