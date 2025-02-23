package com.gcu.agms.service.auth;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.gcu.agms.model.auth.LoginModel;
import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

@DisplayName("LoginService Tests")
class LoginServiceTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private LoginService loginService; // Changed from LoginServiceTest to LoginService
    
    @BeforeEach
    protected void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Should authenticate valid credentials")
    void testAuthenticateValidCredentials() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("testuser");
        loginModel.setPassword("password123");
        
        UserModel user = new UserModel();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole(UserRole.PUBLIC);
        
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.authenticate("testuser", "password123")).thenReturn(true);
        
        // Act
        Optional<UserModel> result = loginService.authenticate(loginModel);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
    
    @Test
    @DisplayName("Should not authenticate invalid credentials")
    void testAuthenticateInvalidCredentials() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("testuser");
        loginModel.setPassword("wrongpassword");
        
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());
        
        // Act
        Optional<UserModel> result = loginService.authenticate(loginModel);
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    @DisplayName("Should validate valid credentials format")
    void testValidateCredentialsValid() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("testuser");
        loginModel.setPassword("password123");
        
        // Act
        boolean result = loginService.validateCredentials(loginModel);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should not validate null credentials")
    void testValidateCredentialsNull() {
        // Act & Assert
        assertFalse(loginService.validateCredentials(null));
    }
    
    @ParameterizedTest(name = "Should not validate credentials: {0}")  
    @MethodSource("invalidCredentialsProvider") // Simplified reference
    @DisplayName("Should validate credential edge cases")
    void testCredentialValidationEdgeCases(String testCase, LoginModel loginModel) {
        // Act
        boolean result = loginService.validateCredentials(loginModel);
        
        // Assert
        assertFalse(result, "Should reject " + testCase);
    }
    
    // Change to public static
    public static Stream<Object[]> invalidCredentialsProvider() {
        return Stream.of(
            new Object[]{"empty credentials", createLoginModel("", "")},
            new Object[]{"null username", createLoginModel(null, "password123")},
            new Object[]{"null password", createLoginModel("testuser", null)},
            new Object[]{"whitespace username", createLoginModel("   ", "password123")},
            new Object[]{"whitespace password", createLoginModel("testuser", "   ")},
            new Object[]{"very long username", createLoginModel("a".repeat(101), "password123")},
            new Object[]{"very long password", createLoginModel("testuser", "a".repeat(101))}
        );
    }
    
    // Keep helper method private
    private static LoginModel createLoginModel(String username, String password) {
        LoginModel model = new LoginModel();
        model.setUsername(username);
        model.setPassword(password);
        return model;
    }
    
    @Test
    @DisplayName("Should handle authentication with non-matching passwords")
    void testAuthenticateNonMatchingPasswords() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("testuser");
        loginModel.setPassword("password123");
        
        UserModel user = new UserModel();
        user.setUsername("testuser");
        user.setPassword("differentpassword");
        user.setRole(UserRole.PUBLIC);
        
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.authenticate("testuser", "password123")).thenReturn(false);
        
        // Act
        Optional<UserModel> result = loginService.authenticate(loginModel);
        
        // Assert
        assertFalse(result.isPresent(), "Should not authenticate with non-matching passwords");
    }

    @Test
    @DisplayName("Should validate credentials with special characters")
    void testValidateCredentialsWithSpecialChars() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("test.user@domain.com");
        loginModel.setPassword("Pass@123!");
        
        // Act
        boolean result = loginService.validateCredentials(loginModel);
        
        // Assert
        assertTrue(result, "Should accept valid credentials with special characters");
    }

    @Test
    @DisplayName("Should handle authentication with disabled user")
    void testAuthenticateDisabledUser() {
        // Arrange
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("disabled");
        loginModel.setPassword("password123");
        
        UserModel user = new UserModel();
        user.setUsername("disabled");
        user.setEnabled(false);
        
        when(userService.findByUsername("disabled")).thenReturn(Optional.of(user));
        
        // Act
        Optional<UserModel> result = loginService.authenticate(loginModel);
        
        // Assert
        assertFalse(result.isPresent(), "Should not authenticate disabled user");
    }
}