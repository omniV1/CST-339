package com.gcu.agms.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.UserModel;
import com.gcu.agms.model.UserRole;

/**
 * Test class for UserService
 * This class tests user management functionality including registration,
 * authentication, and user lookup operations in the Airport Gate Management System.
 */
@DisplayName("User Service Tests")
public class UserServiceTest {
    private UserService userService;

    /**
     * Sets up a new UserService instance and initializes test users before each test.
     * This ensures each test starts with a known set of users in the system.
     */
    @BeforeEach
    @DisplayName("Initialize User Service with Test Data")
    void setUp() {
        userService = new UserService();
        userService.initializeTestUsers();
    }

    /**
     * Tests the registration of a new user.
     * Verifies that a valid user can be registered and then retrieved from the system.
     */
    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterNewUser() {
        // Create a new test user with all required fields
        UserModel newUser = new UserModel();
        newUser.setUsername("testuser");
        newUser.setPassword("Test123!");
        newUser.setEmail("test@test.com");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setPhoneNumber("+1234567890");
        newUser.setRole(UserRole.PUBLIC);

        // Attempt to register the user
        boolean result = userService.registerUser(newUser);
        assertTrue(result, "User registration should succeed");
        
        // Verify the user can be retrieved from the service
        Optional<UserModel> foundUser = userService.findByUsername("testuser");
        assertTrue(foundUser.isPresent(), "Should find registered user");
        assertEquals("testuser", foundUser.get().getUsername(), "Username should match");
        assertEquals("test@test.com", foundUser.get().getEmail(), "Email should match");
        assertEquals(UserRole.PUBLIC, foundUser.get().getRole(), "Role should match");
    }

    /**
     * Tests that duplicate usernames are not allowed during registration.
     * Verifies that the system prevents multiple users with the same username.
     */
    @Test
    @DisplayName("Should prevent duplicate username registration")
    void testRegisterDuplicateUser() {
        // Register first user
        UserModel user1 = new UserModel();
        user1.setUsername("duplicate");
        user1.setPassword("Test123!");
        user1.setEmail("user1@test.com");
        user1.setFirstName("First");
        user1.setLastName("User");
        user1.setPhoneNumber("+1234567890");
        user1.setRole(UserRole.PUBLIC);
        
        boolean firstResult = userService.registerUser(user1);
        assertTrue(firstResult, "First user registration should succeed");

        // Attempt to register second user with same username
        UserModel user2 = new UserModel();
        user2.setUsername("duplicate");
        user2.setPassword("Test456!");
        user2.setEmail("user2@test.com");
        user2.setFirstName("Second");
        user2.setLastName("User");
        user2.setPhoneNumber("+1234567891");
        user2.setRole(UserRole.PUBLIC);
        
        boolean secondResult = userService.registerUser(user2);
        assertFalse(secondResult, "Duplicate username registration should fail");
    }

    /**
     * Tests user authentication with various credentials.
     * Verifies correct authentication behavior for valid and invalid credentials.
     */
    @Test
    @DisplayName("Should authenticate users correctly")
    void testAuthenticate() {
        // Test valid credentials
        assertTrue(userService.authenticate("admin", "Admin123!"),
            "Valid admin credentials should authenticate successfully");
        
        // Test invalid password
        assertFalse(userService.authenticate("admin", "wrongpassword"),
            "Invalid password should fail authentication");
        
        // Test non-existent user
        assertFalse(userService.authenticate("nonexistent", "anypassword"),
            "Non-existent user should fail authentication");
    }

    /**
     * Tests user lookup functionality.
     * Verifies that users can be found by username and that proper roles are assigned.
     */
    @Test
    @DisplayName("Should find users by username correctly")
    void testFindByUsername() {
        // Test finding existing admin user
        Optional<UserModel> adminUser = userService.findByUsername("admin");
        assertTrue(adminUser.isPresent(), "Should find admin user");
        assertEquals(UserRole.ADMIN, adminUser.get().getRole(), 
            "Admin user should have ADMIN role");

        // Test finding non-existent user
        Optional<UserModel> nonexistentUser = userService.findByUsername("nonexistent");
        assertFalse(nonexistentUser.isPresent(), 
            "Should not find non-existent user");
    }
}