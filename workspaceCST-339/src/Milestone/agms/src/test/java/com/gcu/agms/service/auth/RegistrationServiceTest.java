package com.gcu.agms.service.auth;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

@DisplayName("Registration Service Tests")
class RegistrationServiceTest {

    @Mock
    private UserService userService;
    
    @Mock
    private AuthorizationCodeService authCodeService;
    
    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationService(userService, authCodeService);
    }

    @Test
    @DisplayName("Should register normal user successfully")
    void testRegisterNormalUser() {
        UserModel user = new UserModel();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@test.com");
        user.setRole(UserRole.PUBLIC);
        
        when(userService.registerUser(any(UserModel.class))).thenReturn(true);
        
        boolean result = registrationService.registerUser(user);
        assertTrue(result);
        verify(userService).registerUser(user);
        verifyNoInteractions(authCodeService);
    }
    
    @Test
    @DisplayName("Should verify auth code for admin registration") 
    void testRegisterAdminUser() {
        UserModel user = new UserModel();
        user.setUsername("admin");
        user.setPassword("admin123");
        user.setRole(UserRole.ADMIN);
        user.setAuthCode("ADMIN2025");
        
        when(authCodeService.isValidAuthCode("ADMIN2025", UserRole.ADMIN)).thenReturn(true);
        when(userService.registerUser(any(UserModel.class))).thenReturn(true);
        
        boolean result = registrationService.registerUser(user);
        
        assertTrue(result);
        verify(authCodeService).isValidAuthCode("ADMIN2025", UserRole.ADMIN);
        verify(userService).registerUser(user);
    }

    @Test
    @DisplayName("Should reject invalid auth code")
    void testRegisterAdminInvalidCode() {
        UserModel user = new UserModel();
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);
        user.setAuthCode("INVALID");
        
        when(authCodeService.isValidAuthCode("INVALID", UserRole.ADMIN)).thenReturn(false);
        
        boolean result = registrationService.registerUser(user);
        
        assertFalse(result);
        verify(authCodeService).isValidAuthCode("INVALID", UserRole.ADMIN);
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Should validate username availability")
    void testUsernameAvailability() {
        when(userService.findByUsername("available")).thenReturn(Optional.empty());
        when(userService.findByUsername("taken")).thenReturn(Optional.of(new UserModel()));
        
        assertTrue(registrationService.isUsernameAvailable("available"));
        assertFalse(registrationService.isUsernameAvailable("taken"));
        
        verify(userService).findByUsername("available");
        verify(userService).findByUsername("taken");
    }
}