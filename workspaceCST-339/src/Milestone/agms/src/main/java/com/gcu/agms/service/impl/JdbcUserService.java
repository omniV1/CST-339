package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.repository.UserRepository;
import com.gcu.agms.service.auth.UserService;

/**
 * JDBC implementation of the UserService interface.
 * 
 * This service provides the core business logic for user management in the AGMS system.
 * It sits between the controllers and repositories, implementing the application's
 * user management rules and workflows including:
 * 
 * - User registration with validation and password encoding
 * - Password security using Spring Security's PasswordEncoder
 * - User search and retrieval operations
 * - User profile updates with partial data support
 * - User deletion with proper verification
 * 
 * The service follows Spring's best practices:
 * - Dependency injection for repositories and utilities
 * - Clear separation of concerns (validation, data access)
 * - Proper exception handling and logging
 * - Transactional integrity for database operations
 */
@Service("jdbcUserService")
@Primary  // Mark this implementation as the primary one to be autowired when multiple implementations exist
public class JdbcUserService implements UserService {
    /**
     * Logger for recording service operations and errors
     */
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserService.class);
    
    /**
     * Repository for user data access operations
     * Injected by Spring through constructor injection
     */
    private final UserRepository userRepository;
    
    /**
     * Password encoder for securely hashing user passwords
     * Prevents storing plain text passwords in the database
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Constructor with repository and encoder dependency injection.
     * Spring automatically injects the appropriate implementations at runtime.
     * 
     * @param userRepository Repository for user data access operations
     * @param passwordEncoder Encoder for securely hashing passwords
     */
    public JdbcUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("Initialized JDBC User Service");
    }

    /**
     * Registers a new user in the system with validation and security measures.
     * 
     * This method:
     * 1. Validates the user input data for completeness and format
     * 2. Checks for username uniqueness to prevent duplicates
     * 3. Sets creation and update timestamps for auditing
     * 4. Securely encodes the password before storage
     * 5. Persists the validated user to the database
     * 
     * @param newUser The user model containing registration details
     * @return true if registration was successful, false if validation failed or username exists
     */
    @Override
    public boolean registerUser(UserModel newUser) {
        logger.info("Attempting to register new user: {}", newUser.getUsername());
        
        // Validate user data for completeness and format
        if (!validateUser(newUser)) {
            logger.warn("Registration failed: Invalid user data");
            return false;
        }

        // Ensure username uniqueness in the system
        if (userRepository.existsByUsername(newUser.getUsername())) {
            logger.warn("Registration failed: Username already exists");
            return false;
        }
        
        // Set audit timestamps for tracking
        if (newUser.getCreatedAt() == null) {
            newUser.setCreatedAt(LocalDateTime.now());
        }
        if (newUser.getUpdatedAt() == null) {
            newUser.setUpdatedAt(LocalDateTime.now());
        }

        // Encode password for security before saving to database
        // This prevents storing plain text passwords and protects user credentials
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        // Persist the validated and secured user record
        userRepository.save(newUser);
        logger.info("User registered successfully: {}", newUser.getUsername());
        return true;
    }

    /**
     * Finds a user by their username.
     * 
     * This method:
     * 1. Validates the input username
     * 2. Trims whitespace to prevent lookup errors
     * 3. Delegates to the repository for data retrieval
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty Optional otherwise
     */
    @Override
    public Optional<UserModel> findByUsername(String username) {
        // Handle null username to prevent NullPointerException
        if (username == null) {
            return Optional.empty();
        }
        
        // Trim username to prevent lookup errors from whitespace
        String trimmedUsername = username.trim();
        
        // Delegate to repository for actual data access
        return userRepository.findByUsername(trimmedUsername);
    }

    /**
     * Retrieves all users in the system.
     * Typically used for administrative functions.
     * 
     * @return List of all registered users
     */
    @Override
    public List<UserModel> getAllUsers() {
        logger.debug("Retrieving all users");
        return userRepository.findAll();
    }

    /**
     * Deletes a user from the system by their ID.
     * 
     * This method:
     * 1. Verifies the user exists before attempting deletion
     * 2. Performs the deletion operation through the repository
     * 3. Handles any exceptions that occur during the process
     * 
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false if user not found or error occurred
     */
    @Override
    public boolean deleteUser(Long id) {
        try {
            // Verify user exists before attempting deletion
            Optional<UserModel> userToDelete = userRepository.findById(id);
            
            if (userToDelete.isPresent()) {
                // Perform deletion through repository
                userRepository.deleteById(id);
                logger.info("User deleted successfully, ID: {}", id);
                return true;
            }
            
            // User not found case
            logger.warn("User not found for deletion, ID: {}", id);
            return false;
        } catch (Exception e) {
            // Handle any unexpected errors during deletion
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The ID of the user to find
     * @return The user if found, null otherwise
     */
    @Override
    public UserModel getUserById(Long id) {
        logger.debug("Searching for user with ID: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Updates an existing user's information with intelligent merging.
     * 
     * This method:
     * 1. Validates the provided user data
     * 2. Retrieves the existing user record to preserve unchanged fields
     * 3. Selectively updates provided fields while keeping existing data
     * 4. Handles password changes with secure encoding
     * 5. Updates audit timestamp for change tracking
     * 
     * @param userModel The updated user information (may be partial)
     * @return true if update was successful, false if validation failed or user not found
     */
    @Override
    public boolean updateUser(UserModel userModel) {
        logger.info("Attempting to update user: {}", userModel.getUsername());

        // Ensure user ID is provided to identify the record to update
        if (userModel.getId() == null) {
            logger.warn("Update failed: No user ID provided");
            return false;
        }

        // Validate essential fields with less strict validation for updates
        // For updates, some fields may be intentionally left unchanged
        if (userModel.getUsername() == null || userModel.getUsername().trim().isEmpty() ||
            userModel.getEmail() == null || userModel.getEmail().trim().isEmpty() ||
            userModel.getFirstName() == null || userModel.getFirstName().trim().isEmpty() ||
            userModel.getLastName() == null || userModel.getLastName().trim().isEmpty()) {
            logger.warn("Update failed: Invalid user data");
            return false;  
        }
        
        // Retrieve existing user to preserve unchanged fields
        Optional<UserModel> existingUserOpt = userRepository.findById(userModel.getId());
        
        if (existingUserOpt.isPresent()) {
            UserModel existingUser = existingUserOpt.get();
            
            // Update audit timestamp to track when changes were made
            userModel.setUpdatedAt(LocalDateTime.now());
            
            // Handle password update with secure encoding
            // Only encode if a new password is provided
            if (userModel.getPassword() != null && !userModel.getPassword().trim().isEmpty()) {
                // This avoids re-encoding an already encoded password
                // We assume a non-empty password in the update request is intended to be a new password
                userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
            } else {
                // Preserve existing password if not being updated
                userModel.setPassword(existingUser.getPassword());
            }
            
            // Intelligent merging - preserve existing fields if not provided in update
            // This allows partial updates where only some fields are changed
            if (userModel.getPhoneNumber() == null || userModel.getPhoneNumber().trim().isEmpty()) {
                userModel.setPhoneNumber(existingUser.getPhoneNumber());
            }
            if (userModel.getRole() == null) {
                userModel.setRole(existingUser.getRole());
            }
            if (userModel.isActive() == null) {
                userModel.setActive(existingUser.isActive());
            }
            if (userModel.getCreatedAt() == null) {
                userModel.setCreatedAt(existingUser.getCreatedAt());
            }
            if (!userModel.isEnabled()) {
                userModel.setEnabled(existingUser.isEnabled());
            }
            
            // Save the merged user data to the database
            userRepository.save(userModel);
            logger.info("User updated successfully: {}", userModel.getUsername());
            return true;
        }
        
        // Handle case where user doesn't exist
        logger.warn("User not found for update with ID: {}", userModel.getId());
        return false;
    }
    
    /**
     * Validates a user model for data integrity and security requirements.
     * 
     * This method performs comprehensive validation:
     * 1. Checks for presence of required fields
     * 2. Validates email format using regex pattern
     * 3. Validates phone number format
     * 4. Enforces password complexity requirements
     *    - Minimum length of 8 characters
     *    - Contains at least one uppercase letter
     *    - Contains at least one lowercase letter
     *    - Contains at least one digit
     *    - Contains at least one special character
     * 
     * The validation logic adjusts based on whether this is a new user registration
     * or an update to an existing user.
     * 
     * @param user The user model to validate
     * @return true if all validation checks pass, false otherwise
     */
    private boolean validateUser(UserModel user) {
        // Validate basic required fields are present
        if (user == null || 
            user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() || 
            user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
            user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            return false;
        }

        // Email validation using safe regex pattern
        // Ensures email follows standard format (username@domain.tld)
        if (!user.getEmail().matches("^[\\w+.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return false;
        }

        // Additional validation for new user registrations
        // For updates (when ID exists), these fields may be optional
        if (user.getId() == null) { // This is a new registration
            // Require password and phone for new registrations
            if (user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
                return false;
            }

            // Phone validation - international format with country code
            // Format: +CountryCode followed by 7-14 digits
            if (!user.getPhoneNumber().matches("^\\+?[1-9]\\d{7,14}$")) {
                return false;
            }

            // Password complexity validation
            String password = user.getPassword();
            
            // Length requirement - minimum 8 characters
            if (password.length() < 8) {
                return false;
            }

            // Character class requirements using optimized regex patterns
            // Using possessive quantifiers (*+) for performance and security
            boolean hasUpper = password.matches("^[^A-Z]*+[A-Z][\\s\\S]*+$");  // Has uppercase letter
            boolean hasLower = password.matches("^[^a-z]*+[a-z][\\s\\S]*+$");  // Has lowercase letter
            boolean hasDigit = password.matches("^\\D*+\\d[\\s\\S]*+$");       // Has digit
            boolean hasSpecial = password.matches("^[^@#$%^&+=!]*+[@#$%^&+=!][\\s\\S]*+$"); // Has special char

            return hasUpper && hasLower && hasDigit && hasSpecial;
        }

        // Validation for user updates (when ID exists)
        // Only validate fields that are provided in the update
        
        // Phone validation - only if provided in update
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!user.getPhoneNumber().matches("^\\+?[1-9]\\d{7,14}$")) {
                return false;
            }
        }

        // Password validation - only if provided in update
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            String password = user.getPassword();
            
            // Length requirement
            if (password.length() < 8) {
                return false;
            }

            // Character class requirements
            boolean hasUpper = password.matches("^[^A-Z]*+[A-Z][\\s\\S]*+$");
            boolean hasLower = password.matches("^[^a-z]*+[a-z][\\s\\S]*+$");
            boolean hasDigit = password.matches("^\\D*+\\d[\\s\\S]*+$");
            boolean hasSpecial = password.matches("^[^@#$%^&+=!]*+[@#$%^&+=!][\\s\\S]*+$");

            if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
                return false;
            }
        }

        // All validation checks passed
        return true;
    }
}