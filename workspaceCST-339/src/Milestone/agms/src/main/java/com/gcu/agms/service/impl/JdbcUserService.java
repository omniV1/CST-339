package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.repository.UserRepository;
import com.gcu.agms.service.auth.UserService;

/**
 * JDBC implementation of the UserService interface.
 * This service uses a UserRepository to access and manage user data from a database.
 */
@Service("jdbcUserService")
@Primary  // Mark this implementation as the primary one to be used
public class JdbcUserService implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserService.class);
    
    private final UserRepository userRepository;
    
    /**
     * Constructor with repository dependency injection.
     * 
     * @param userRepository Repository for user data access
     */
    public JdbcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("Initialized JDBC User Service");
    }

    @Override
    public boolean registerUser(UserModel newUser) {
        logger.info("Attempting to register new user: {}", newUser.getUsername());
        
        // Validate user data
        if (!validateUser(newUser)) {
            logger.warn("Registration failed: Invalid user data");
            return false;
        }

        // Check if username already exists
        if (userRepository.existsByUsername(newUser.getUsername())) {
            logger.warn("Registration failed: Username already exists");
            return false;
        }
        
        // Set timestamps if not set
        if (newUser.getCreatedAt() == null) {
            newUser.setCreatedAt(LocalDateTime.now());
        }
        if (newUser.getUpdatedAt() == null) {
            newUser.setUpdatedAt(LocalDateTime.now());
        }
        
        // Save user to database
        userRepository.save(newUser);
        logger.info("User registered successfully: {}", newUser.getUsername());
        return true;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        
        // Trim username before searching
        String trimmedUsername = username.trim();
        
        return userRepository.findByUsername(trimmedUsername);
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        // Trim username before looking up
        String trimmedUsername = username.trim();
        
        Optional<UserModel> user = findByUsername(trimmedUsername);
        if (user.isPresent()) {
            boolean authenticated = user.get().getPassword().equals(password);
            
            if (authenticated) {
                // Update last login time
                userRepository.updateLastLogin(user.get().getId(), LocalDateTime.now());
            }
            
            return authenticated;
        }
        return false;
    }

    @Override
    public List<UserModel> getAllUsers() {
        logger.debug("Retrieving all users");
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            Optional<UserModel> userToDelete = userRepository.findById(id);
            
            if (userToDelete.isPresent()) {
                userRepository.deleteById(id);
                logger.info("User deleted successfully, ID: {}", id);
                return true;
            }
            logger.warn("User not found for deletion, ID: {}", id);
            return false;
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UserModel getUserById(Long id) {
        logger.debug("Searching for user with ID: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean updateUser(UserModel userModel) {
        logger.info("Attempting to update user: {}", userModel.getUsername());

        // Check if the user ID is provided
        if (userModel.getId() == null) {
            logger.warn("Update failed: No user ID provided");
            return false;
        }

        // Validate update data - with less strict validation for updates
        if (userModel.getUsername() == null || userModel.getUsername().trim().isEmpty() ||
            userModel.getEmail() == null || userModel.getEmail().trim().isEmpty() ||
            userModel.getFirstName() == null || userModel.getFirstName().trim().isEmpty() ||
            userModel.getLastName() == null || userModel.getLastName().trim().isEmpty()) {
            logger.warn("Update failed: Invalid user data");
            return false;  
        }
        
        Optional<UserModel> existingUser = userRepository.findById(userModel.getId());
        
        if (existingUser.isPresent()) {
            // Update updatedAt timestamp
            userModel.setUpdatedAt(LocalDateTime.now());
            
            // Preserve the password if not provided in the update
            if (userModel.getPassword() == null || userModel.getPassword().trim().isEmpty()) {
                userModel.setPassword(existingUser.get().getPassword());
            }
            
            // Preserve other fields if not provided in the update
            if (userModel.getPhoneNumber() == null || userModel.getPhoneNumber().trim().isEmpty()) {
                userModel.setPhoneNumber(existingUser.get().getPhoneNumber());
            }
            if (userModel.getRole() == null) {
                userModel.setRole(existingUser.get().getRole());
            }
            if (userModel.isActive() == null) {
                userModel.setActive(existingUser.get().isActive());
            }
            if (userModel.getCreatedAt() == null) {
                userModel.setCreatedAt(existingUser.get().getCreatedAt());
            }
            if (!userModel.isEnabled()) {
                userModel.setEnabled(existingUser.get().isEnabled());
            }
            
            userRepository.save(userModel);
            logger.info("User updated successfully: {}", userModel.getUsername());
            return true;
        }
        
        logger.warn("User not found for update with ID: {}", userModel.getId());
        return false;
    }
    
    /**
     * Validates a user model for registration.
     * 
     * @param user The user model to validate
     * @return true if valid, false otherwise
     */
    private boolean validateUser(UserModel user) {
        if (user == null || 
            user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() || 
            user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
            user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return false;
        }

        // Email validation - using safe regex pattern
        if (!user.getEmail().matches("^[\\w+.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return false;
        }

        // For registrations, password is required
        if (user.getId() == null) {
            if (user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
                return false;
            }

            // Phone validation - using safe regex pattern
            if (!user.getPhoneNumber().matches("^\\+?[1-9]\\d{7,14}$")) {
                return false;
            }

            // Password validation using character class checks
            String password = user.getPassword();
            if (password.length() < 8) {
                return false;
            }

            // Use possessive quantifiers, anchors, and concise character classes
            boolean hasUpper = password.matches("^[^A-Z]*+[A-Z][\\s\\S]*+$");
            boolean hasLower = password.matches("^[^a-z]*+[a-z][\\s\\S]*+$");
            boolean hasDigit = password.matches("^\\D*+\\d[\\s\\S]*+$");  // Using \D and \d
            boolean hasSpecial = password.matches("^[^@#$%^&+=!]*+[@#$%^&+=!][\\s\\S]*+$");

            return hasUpper && hasLower && hasDigit && hasSpecial;
        }

        // For updates, if phone number is provided, validate it
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!user.getPhoneNumber().matches("^\\+?[1-9]\\d{7,14}$")) {
                return false;
            }
        }

        // For updates, if password is provided, validate it
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            String password = user.getPassword();
            if (password.length() < 8) {
                return false;
            }

            // Use possessive quantifiers, anchors, and concise character classes
            boolean hasUpper = password.matches("^[^A-Z]*+[A-Z][\\s\\S]*+$");
            boolean hasLower = password.matches("^[^a-z]*+[a-z][\\s\\S]*+$");
            boolean hasDigit = password.matches("^\\D*+\\d[\\s\\S]*+$");  // Using \D and \d
            boolean hasSpecial = password.matches("^[^@#$%^&+=!]*+[@#$%^&+=!][\\s\\S]*+$");

            if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
                return false;
            }
        }

        return true;
    }
}