package com.gcu.agms.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

@DisplayName("InMemoryUserService Tests")
 class InMemoryUserServiceTest {

    private InMemoryUserService userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserService();
        userService.initializeTestUsers();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUser() {
        UserModel newUser = new UserModel();
        newUser.setUsername("testuser");
        newUser.setPassword("Test123!");
        newUser.setEmail("test@test.com");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setRole(UserRole.PUBLIC);

        boolean result = userService.registerUser(newUser);
        assertTrue(result, "User registration should succeed");

        Optional<UserModel> savedUser = userService.findByUsername("testuser");
        assertTrue(savedUser.isPresent(), "User should be found after registration");
        assertEquals("test@test.com", savedUser.get().getEmail(), "Email should match");
    }

    @Test
    @DisplayName("Should not register user with duplicate username")
    void testRegisterDuplicateUser() {
        UserModel user1 = new UserModel();
        user1.setUsername("duplicate");
        user1.setPassword("Test123!");
        user1.setRole(UserRole.PUBLIC);

        assertTrue(userService.registerUser(user1), "First registration should succeed");
        assertFalse(userService.registerUser(user1), "Duplicate registration should fail");
    }

    @Test
    @DisplayName("Should authenticate valid credentials")
    void testAuthenticate() {
        assertTrue(userService.authenticate("admin", "Admin123!"), 
            "Admin should authenticate with correct credentials");
        assertFalse(userService.authenticate("admin", "wrongpass"), 
            "Admin should not authenticate with wrong password");
        assertFalse(userService.authenticate("nonexistent", "pass"), 
            "Non-existent user should not authenticate");
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        Optional<UserModel> adminUser = userService.findByUsername("admin");
        assertTrue(adminUser.isPresent(), "Should find admin user");
        assertEquals(UserRole.ADMIN, adminUser.get().getRole(), "Admin should have ADMIN role");

        Optional<UserModel> nonExistentUser = userService.findByUsername("nonexistent");
        assertFalse(nonExistentUser.isPresent(), "Should not find non-existent user");
    }

    @Test
    @DisplayName("Should handle null username and password in authentication")
    void testAuthenticateNullCredentials() {
        assertFalse(userService.authenticate(null, "password"), 
            "Null username should not authenticate");
        assertFalse(userService.authenticate("admin", null), 
            "Null password should not authenticate");
        assertFalse(userService.authenticate(null, null), 
            "Null credentials should not authenticate");
    }

    @Test
    @DisplayName("Should handle empty username and password in authentication")
    void testAuthenticateEmptyCredentials() {
        assertFalse(userService.authenticate("", "password"), 
            "Empty username should not authenticate");
        assertFalse(userService.authenticate("admin", ""), 
            "Empty password should not authenticate");
        assertFalse(userService.authenticate("", ""), 
            "Empty credentials should not authenticate");
    }
}