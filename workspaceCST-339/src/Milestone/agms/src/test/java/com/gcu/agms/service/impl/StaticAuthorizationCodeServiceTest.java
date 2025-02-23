package com.gcu.agms.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.auth.UserRole;

@DisplayName("Static Authorization Code Service Tests")
class StaticAuthorizationCodeServiceTest {

    private StaticAuthorizationCodeService authCodeService;

    @BeforeEach
    protected void setUp() {
        authCodeService = new StaticAuthorizationCodeService();
    }

    @Test
    @DisplayName("Should validate admin authorization code")
    void testValidAdminAuthCode() {
        assertTrue(authCodeService.isValidAuthCode("ADMIN2025", UserRole.ADMIN),
            "Valid admin code should be accepted");
        assertFalse(authCodeService.isValidAuthCode("ADMIN2025", UserRole.OPERATIONS_MANAGER),
            "Admin code should not work for operations manager");
    }

    @Test
    @DisplayName("Should validate operations manager authorization code")
    void testValidOpsAuthCode() {
        assertTrue(authCodeService.isValidAuthCode("OPS2025", UserRole.OPERATIONS_MANAGER),
            "Valid ops code should be accepted");
        assertFalse(authCodeService.isValidAuthCode("OPS2025", UserRole.ADMIN),
            "Ops code should not work for admin");
    }

    @Test
    @DisplayName("Should reject invalid authorization codes")
    void testInvalidAuthCodes() {
        assertFalse(authCodeService.isValidAuthCode("INVALID", UserRole.ADMIN),
            "Invalid code should be rejected");
        assertFalse(authCodeService.isValidAuthCode("", UserRole.ADMIN),
            "Empty code should be rejected");
        assertFalse(authCodeService.isValidAuthCode(null, UserRole.ADMIN),
            "Null code should be rejected");
    }

    @Test
    @DisplayName("Should reject invalid roles")
    void testInvalidRoles() {
        assertFalse(authCodeService.isValidAuthCode("ADMIN2025", UserRole.PUBLIC),
            "Admin code should not work for public role");
        assertFalse(authCodeService.isValidAuthCode("OPS2025", UserRole.GATE_MANAGER),
            "Ops code should not work for gate manager role");
        assertFalse(authCodeService.isValidAuthCode("ADMIN2025", null),
            "Valid code should not work for null role");
    }

    @Test
    @DisplayName("Should initialize with predefined codes")
    void testServiceInitialization() {
        // Test init() method
        authCodeService.init();
        
        // Verify predefined codes work
        assertTrue(authCodeService.isValidAuthCode("ADMIN2025", UserRole.ADMIN));
        assertTrue(authCodeService.isValidAuthCode("OPS2025", UserRole.OPERATIONS_MANAGER));
        
        // Verify code count
        assertTrue(authCodeService.isValidAuthCode("ADMIN2025", UserRole.ADMIN) &&
                  authCodeService.isValidAuthCode("OPS2025", UserRole.OPERATIONS_MANAGER),
                  "Service should be initialized with both admin and ops codes");
    }

    @Test
    @DisplayName("Should handle code case sensitivity")
    void testCodeCaseSensitivity() {
        assertFalse(authCodeService.isValidAuthCode("admin2025", UserRole.ADMIN),
            "Auth codes should be case sensitive");
        assertFalse(authCodeService.isValidAuthCode("ops2025", UserRole.OPERATIONS_MANAGER),
            "Auth codes should be case sensitive");
    }
}