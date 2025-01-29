package com.gcu.agms.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.UserRole;

/**
 * Test class for AuthorizationCodeService
 * This class tests the validation of authorization codes for different user roles
 * in the Airport Gate Management System.
 */
@DisplayName("Authorization Code Service Tests")
public class AuthorizationCodeServiceTest {
    private AuthorizationCodeService authService;

    /**
     * Sets up a new AuthorizationCodeService instance before each test.
     * This ensures each test starts with a fresh service instance,
     * preventing any potential state interference between tests.
     */
    @BeforeEach
    @DisplayName("Initialize Authorization Service")
    void setUp() {
        authService = new AuthorizationCodeService();
    }

    /**
     * Tests that a valid admin authorization code is accepted.
     * The code "ADMIN2025" should be recognized as valid for the ADMIN role.
     */
    @Test
    @DisplayName("Valid admin authorization code should be accepted")
    void testValidAdminCode() {
        assertTrue(authService.isValidAuthCode("ADMIN2025", UserRole.ADMIN),
                 "Admin code 'ADMIN2025' should be validated successfully");
    }

    /**
     * Tests that a valid operations manager authorization code is accepted.
     * The code "OPS2025" should be recognized as valid for the OPERATIONS_MANAGER role.
     */
    @Test
    @DisplayName("Valid operations manager authorization code should be accepted")
    void testValidOpsCode() {
        assertTrue(authService.isValidAuthCode("OPS2025", UserRole.OPERATIONS_MANAGER),
                 "Operations manager code 'OPS2025' should be validated successfully");
    }

    /**
     * Tests that an invalid authorization code is rejected.
     * Any code not specifically configured in the service should be rejected.
     */
    @Test
    @DisplayName("Invalid authorization code should be rejected")
    void testInvalidCode() {
        assertFalse(authService.isValidAuthCode("INVALID", UserRole.ADMIN),
                  "Invalid authorization code should be rejected");
    }

    /**
     * Tests that a null authorization code is rejected.
     * This ensures the service properly handles null input values.
     */
    @Test
    @DisplayName("Null authorization code should be rejected")
    void testNullCode() {
        assertFalse(authService.isValidAuthCode(null, UserRole.ADMIN),
                  "Null authorization code should be rejected");
    }

    /**
     * Tests that an empty authorization code is rejected.
     * This ensures the service properly handles empty string input.
     */
    @Test
    @DisplayName("Empty authorization code should be rejected")
    void testEmptyCode() {
        assertFalse(authService.isValidAuthCode("", UserRole.ADMIN),
                  "Empty authorization code should be rejected");
    }

    /**
     * Tests that a valid code used with the wrong role is rejected.
     * This ensures codes are properly mapped to their specific roles.
     */
    @Test
    @DisplayName("Valid code with wrong role should be rejected")
    void testWrongRoleForValidCode() {
        assertFalse(authService.isValidAuthCode("ADMIN2025", UserRole.OPERATIONS_MANAGER),
                  "Admin code should not be valid for operations manager role");
    }
}