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
    public void setUp() {
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
        newUser.setPhoneNumber("+1234567890"); // Add required phone number
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
        user1.setEmail("duplicate@test.com"); // Add required email
        user1.setFirstName("Test");           // Add required first name
        user1.setLastName("User");            // Add required last name
        user1.setPhoneNumber("+1234567890");  // Add required phone number
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

    @Test
    @DisplayName("Should validate user registration data")
    void testValidateUserRegistration() {
        UserModel invalidUser = new UserModel();
        // Test empty fields
        assertFalse(userService.registerUser(invalidUser), 
            "Should not register user with empty fields");
        
        // Test invalid email format
        invalidUser.setUsername("testuser");
        invalidUser.setPassword("Test123!");
        invalidUser.setEmail("invalidemail");
        invalidUser.setFirstName("Test");
        invalidUser.setLastName("User");
        invalidUser.setRole(UserRole.PUBLIC);
        assertFalse(userService.registerUser(invalidUser), 
            "Should not register user with invalid email");
        
        // Test password requirements
        invalidUser.setEmail("valid@email.com");
        invalidUser.setPassword("weak");
        assertFalse(userService.registerUser(invalidUser), 
            "Should not register user with weak password");
    }

    @Test
    @DisplayName("Should update user information")
    void testUpdateUser() {
        // Create and register initial user
        UserModel user = new UserModel();
        user.setUsername("updatetest");
        user.setPassword("Test123!");
        user.setEmail("initial@test.com");
        user.setFirstName("Initial");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.PUBLIC);
        
        assertTrue(userService.registerUser(user), "Initial registration should succeed");
        
        // Get user ID for update
        Optional<UserModel> savedUser = userService.findByUsername("updatetest");
        assertTrue(savedUser.isPresent());
        Long userId = savedUser.get().getId();
        
        // Create update with all required fields
        UserModel update = new UserModel();
        update.setId(userId);
        update.setUsername("updatetest"); // Keep same username
        update.setPassword("Test123!");   // Keep same password 
        update.setEmail("updated@test.com"); // New email
        update.setFirstName("Updated");      // New first name
        update.setLastName("User");          // Keep same last name
        update.setPhoneNumber("+1234567890"); // Keep same phone
        update.setRole(UserRole.PUBLIC);      // Keep same role
        
        assertTrue(userService.updateUser(update), "User update should succeed");
        
        // Verify updates
        Optional<UserModel> updatedUser = userService.findByUsername("updatetest");
        assertTrue(updatedUser.isPresent(), "Updated user should exist");
        assertEquals("updated@test.com", updatedUser.get().getEmail(), "Email should be updated");
        assertEquals("Updated", updatedUser.get().getFirstName(), "First name should be updated");
    }

    @Test
    @DisplayName("Should handle invalid user updates")
    void testInvalidUserUpdates() {
        // Create and register initial user
        UserModel user = new UserModel();
        user.setUsername("updatetest");
        user.setPassword("Test123!");
        user.setEmail("initial@test.com");
        user.setFirstName("Initial");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.PUBLIC);
        
        assertTrue(userService.registerUser(user), "Initial registration should succeed");
        
        // Get user ID
        Optional<UserModel> savedUser = userService.findByUsername("updatetest");
        assertTrue(savedUser.isPresent());
        Long userId = savedUser.get().getId();
        
        // Test update with invalid email
        UserModel invalidEmail = new UserModel();
        invalidEmail.setId(userId);
        invalidEmail.setUsername("updatetest");
        invalidEmail.setPassword("Test123!");
        invalidEmail.setEmail("invalid-email");
        invalidEmail.setFirstName("Updated");
        invalidEmail.setLastName("User");
        invalidEmail.setPhoneNumber("+1234567890");
        invalidEmail.setRole(UserRole.PUBLIC);
        
        assertFalse(userService.updateUser(invalidEmail), 
            "Update with invalid email should fail");
        
        // Test update with invalid phone
        UserModel invalidPhone = new UserModel();
        invalidPhone.setId(userId);
        invalidPhone.setUsername("updatetest");
        invalidPhone.setPassword("Test123!");
        invalidPhone.setEmail("valid@test.com");
        invalidPhone.setFirstName("Updated");
        invalidPhone.setLastName("User");
        invalidPhone.setPhoneNumber("invalid-phone");
        invalidPhone.setRole(UserRole.PUBLIC);
        
        assertFalse(userService.updateUser(invalidPhone), 
            "Update with invalid phone should fail");
    }

    @Test
    @DisplayName("Should handle user deletion")
    void testDeleteUser() {
        // Create and register user
        UserModel user = new UserModel();
        user.setUsername("deletetest");
        user.setPassword("Test123!");
        user.setEmail("delete@test.com");     // Add required email  
        user.setFirstName("Delete");          // Add required first name
        user.setLastName("User");             // Add required last name
        user.setPhoneNumber("+1234567890");   // Add required phone number
        user.setRole(UserRole.PUBLIC);
        
        assertTrue(userService.registerUser(user), "User registration should succeed");
        
        // Get the user's ID from the saved user
        Optional<UserModel> savedUser = userService.findByUsername("deletetest");
        assertTrue(savedUser.isPresent(), "User should exist before deletion");
        
        // Delete using the user's ID
        Long userId = savedUser.get().getId();
        assertTrue(userService.deleteUser(userId), "User deletion should succeed");
        
        assertFalse(userService.findByUsername("deletetest").isPresent(), 
            "Deleted user should not exist");
        assertFalse(userService.deleteUser(userId), 
            "Deleting non-existent user should fail");
    }

    @Test
    @DisplayName("Should handle edge cases in user deletion")
    void testEdgeCaseDeletion() {
        // Test deleting with null ID
        assertFalse(userService.deleteUser(null), 
            "Deletion with null ID should fail");
    }

    @Test
    @DisplayName("Should retrieve user roles correctly")
    void testUserRoles() {
        // Test admin role
        Optional<UserModel> adminUser = userService.findByUsername("admin");
        assertTrue(adminUser.isPresent(), "Admin user should exist");
        assertEquals(UserRole.ADMIN, adminUser.get().getRole(), "Admin should have ADMIN role");
        
        // Test airline staff role
        Optional<UserModel> airlineUser = userService.findByUsername("airline");
        assertTrue(airlineUser.isPresent(), "Airline user should exist");
        assertEquals(UserRole.AIRLINE_STAFF, airlineUser.get().getRole(), 
            "Airline user should have AIRLINE_STAFF role");
        
        // Test public role
        UserModel publicUser = new UserModel();
        publicUser.setUsername("publicuser");
        publicUser.setPassword("Test123!");
        publicUser.setEmail("public@test.com");   // Add required email
        publicUser.setFirstName("Public");        // Add required first name
        publicUser.setLastName("User");           // Add required last name
        publicUser.setPhoneNumber("+1234567890"); // Add required phone number
        publicUser.setRole(UserRole.PUBLIC);
        
        assertTrue(userService.registerUser(publicUser), "Public user registration should succeed");
        Optional<UserModel> savedPublicUser = userService.findByUsername("publicuser");
        assertTrue(savedPublicUser.isPresent(), "Public user should exist");
        assertEquals(UserRole.PUBLIC, savedPublicUser.get().getRole(), 
            "Public user should have PUBLIC role");
    }

    @Test
    @DisplayName("Should validate password requirements")
    void testPasswordValidation() {
        UserModel user = new UserModel();
        user.setUsername("passwordtest");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User"); 
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.PUBLIC);
        
        // Test short password
        user.setPassword("short");
        assertFalse(userService.registerUser(user), "Short password should fail");
        
        // Test password without uppercase
        user.setPassword("password123!");
        assertFalse(userService.registerUser(user), "Password without uppercase should fail");
        
        // Test password without lowercase
        user.setPassword("PASSWORD123!");
        assertFalse(userService.registerUser(user), "Password without lowercase should fail");
        
        // Test password without numbers
        user.setPassword("Password!");
        assertFalse(userService.registerUser(user), "Password without numbers should fail");
        
        // Test valid password
        user.setPassword("Password123!");
        assertTrue(userService.registerUser(user), "Valid password should succeed");
    }

    @Test
    @DisplayName("Should handle special authentication cases")
    void testSpecialAuthenticationCases() {
        // Test case sensitivity in username
        assertFalse(userService.authenticate("ADMIN", "Admin123!"), 
            "Username should be case sensitive");
        
        // Test trimmed whitespace
        assertTrue(userService.authenticate("admin ", "Admin123!"), 
            "Username should be trimmed");
        assertTrue(userService.authenticate(" admin", "Admin123!"), 
            "Username should be trimmed");
        
        // Test password with spaces
        UserModel userWithSpaces = new UserModel();
        userWithSpaces.setUsername("spacestest");
        userWithSpaces.setPassword("Test 123!");
        userWithSpaces.setEmail("spaces@test.com");
        userWithSpaces.setFirstName("Test");
        userWithSpaces.setLastName("User");
        userWithSpaces.setPhoneNumber("+1234567890");
        userWithSpaces.setRole(UserRole.PUBLIC);
        
        assertTrue(userService.registerUser(userWithSpaces), 
            "Registration with space in password should succeed");
        assertTrue(userService.authenticate("spacestest", "Test 123!"), 
            "Authentication with space in password should succeed");
    }
}