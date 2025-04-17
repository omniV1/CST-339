package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.auth.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

/**
 * RegisterController handles the registration process for new users in the Airport Gate Management System.
 * 
 * This controller follows the MVC pattern:
 * - Model: UserModel class (contains registration form data)
 * - View: register.html template
 * - Controller: This RegisterController class
 * 
 * Key responsibilities:
 * - Display registration form to new users
 * - Process form submissions with data validation
 * - Validate authorization codes for special roles (Admin, Operations Manager)
 * - Register new users in the system through UserService
 * - Handle success and error scenarios with appropriate redirects and messages
 * 
 * Endpoints:
 * - GET /register: Displays the registration page with an empty form
 * - POST /register: Processes the completed registration form
 * 
 * Dependencies:
 * - UserService: Handles user-related operations including registration
 * - AuthorizationCodeService: Validates authorization codes for restricted roles
 */
@Controller
@RequestMapping({"/", "/auth"}) // Handle both root and /auth paths for flexibility in URL structure
@Tag(name = "Authentication", description = "Authentication endpoints for user login and registration")
public class RegisterController {
    /**
     * Logger instance for this class, used to record application events
     * and debug information throughout the registration process.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    
    /**
     * Constants for view names and model attributes to avoid hardcoding strings
     * throughout the controller methods. Improves maintainability and prevents typos.
     */
    private static final String REGISTER_VIEW = "register";                  // View name for registration page
    private static final String LOGIN_REDIRECT = "redirect:/auth/login";     // Redirect URL to login page
    private static final String REGISTER_REDIRECT = "redirect:/register";    // Redirect URL back to registration page
    private static final String USER_MODEL_ATTR = "userModel";               // Attribute name for the user model
    private static final String PAGE_TITLE_ATTR = "pageTitle";               // Attribute name for page title
    private static final String ERROR_ATTR = "error";                        // Attribute name for error messages
    private static final String SUCCESS_ATTR = "successMessage";             // Attribute name for success messages
    
    /**
     * UserService dependency - handles all user-related business logic
     * including registration, credential validation, and persistence.
     */
    private final UserService userService;
    
    /**
     * AuthorizationCodeService dependency - verifies authorization codes
     * for restricted roles like Admin and Operations Manager.
     */
    private final AuthorizationCodeService authCodeService;
    
    /**
     * Constructor with dependency injection for required services.
     * Spring automatically injects implementations of these services at runtime.
     *
     * @param userService Service for user management operations
     * @param authCodeService Service for authorization code validation
     */
    public RegisterController(UserService userService, AuthorizationCodeService authCodeService) {
        this.userService = userService;
        this.authCodeService = authCodeService;
    }
    
    /**
     * Returns registration form information and structure
     */
    @GetMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Get registration form information",
        description = "Returns the registration form structure and required fields"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved registration form information",
            content = @Content(mediaType = "application/json")
        )
    })
    public Map<String, Object> getRegistrationInfo() {
        Map<String, Object> registrationInfo = new HashMap<>();
        registrationInfo.put("pageTitle", "Register - AGMS");
        registrationInfo.put("formFields", new String[]{
            "username",
            "password",
            "confirmPassword",
            "firstName",
            "lastName",
            "email",
            "role",
            "authCode"
        });
        registrationInfo.put("availableRoles", UserRole.values());
        registrationInfo.put("method", "POST");
        registrationInfo.put("action", "/register");
        return registrationInfo;
    }
    
    /**
     * Processes user registration
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully registered user",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid registration data or authorization code",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Map<String, Object>> processRegistration(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserModel userModel) {
        
        logger.info("Processing registration request for user: {}", userModel.getUsername());
        
        // Validate authorization code for special roles
        if ((userModel.getRole() == UserRole.ADMIN || 
             userModel.getRole() == UserRole.OPERATIONS_MANAGER) &&
            !authCodeService.isValidAuthCode(userModel.getAuthCode(), userModel.getRole())) {
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid authorization code for the selected role");
            return ResponseEntity.badRequest().body(response);
        }

        // Set default role if none provided
        userModel.setRole(userModel.getRole() == null ? UserRole.PUBLIC : userModel.getRole());
        
        // Attempt registration
        boolean registrationSuccess = userService.registerUser(userModel);
        
        Map<String, Object> response = new HashMap<>();
        if (registrationSuccess) {
            String successMessage = switch(userModel.getRole()) {
                case ADMIN -> "Administrator account created successfully. You now have full system access.";
                case OPERATIONS_MANAGER -> "Operations Manager account created. You can now manage flight operations.";
                case GATE_MANAGER -> "Gate Manager account created. You can now manage gate assignments.";
                case AIRLINE_STAFF -> "Airline Staff account created. You can now view flight information.";
                case PUBLIC -> "Account created successfully. Welcome to AGMS!";
            };
            
            response.put("success", true);
            response.put("message", successMessage);
            response.put("redirectUrl", "/login");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Registration failed. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
