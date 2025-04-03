package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.auth.UserService;

import jakarta.validation.Valid;

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
     * Handles GET requests to the /register endpoint.
     * Displays the registration form page to the user.
     * 
     * This method:
     * 1. Creates an empty UserModel if one doesn't exist in the model
     * 2. Sets the page title for the view
     * 3. Returns the logical view name to be resolved to register.html
     * 
     * @param model Spring MVC Model object to pass data to the view
     * @return String representing the logical view name
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Initialize empty user model for the form if not already present
        if (!model.containsAttribute(USER_MODEL_ATTR)) {
            model.addAttribute(USER_MODEL_ATTR, new UserModel());
        }
        
        // Set page title for the browser tab
        model.addAttribute(PAGE_TITLE_ATTR, "Register - AGMS");
        
        // Return the view name to be resolved to the template
        return REGISTER_VIEW;
    }
    
    /**
     * Handles POST requests to the /register endpoint.
     * Processes the user registration form submission.
     * 
     * This method:
     * 1. Validates the form data (through @Valid annotation)
     * 2. Validates authorization codes for special roles
     * 3. Sets a default role if none selected
     * 4. Attempts to register the user via UserService
     * 5. Handles success or failure with appropriate redirects and messages
     * 
     * @param userModel Form data bound to UserModel object (validated by @Valid)
     * @param bindingResult Contains validation errors if any occur
     * @param redirectAttributes Used to pass attributes through redirects
     * @return String representing the redirect URL based on operation result
     */
    @PostMapping("/register") // Changed from /doRegister to /register for consistency
    public String processRegistration(
            @Valid @ModelAttribute(USER_MODEL_ATTR) UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        // Log the registration attempt with username for audit trail
        logger.info("Processing registration request for user: {}", userModel.getUsername());
        
        /**
         * Role authorization validation
         * 
         * Special roles (Admin, Operations Manager) require valid authorization codes.
         * This prevents unauthorized users from registering with elevated privileges.
         */
        if ((userModel.getRole() == UserRole.ADMIN || 
             userModel.getRole() == UserRole.OPERATIONS_MANAGER) &&
            !authCodeService.isValidAuthCode(userModel.getAuthCode(), userModel.getRole())) {
            
            // Add field-specific error for invalid authorization code
            bindingResult.rejectValue("authCode", "invalid.authCode", 
                "Invalid authorization code for the selected role.");
            return REGISTER_VIEW;
        }
        
        /**
         * Form validation check
         * 
         * If the @Valid annotation found any constraint violations,
         * return to the registration form with validation errors.
         */
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors found during registration");
            return REGISTER_VIEW;
        }
        
        /**
         * Default role assignment
         * 
         * If no role was selected, default to PUBLIC role.
         * This ensures every user has an assigned role.
         */
        userModel.setRole(userModel.getRole() == null ? UserRole.PUBLIC : userModel.getRole());
        logger.info("Role set to: {}", userModel.getRole());
        logger.info("Attempting to register user...");
        
        /**
         * User registration attempt
         * 
         * Call UserService to attempt registration.
         * This will typically:
         * 1. Check if username already exists
         * 2. Hash the password for secure storage
         * 3. Persist the user data to the database
         */
        boolean registrationSuccess = userService.registerUser(userModel);
        
        /**
         * Handle registration result
         * 
         * On success: Redirect to login page with role-specific success message
         * On failure: Redirect back to registration form with error message
         */
        if (registrationSuccess) {
            logger.info("Registration successful for user: {}", userModel.getUsername());
            
            // Create role-specific success messages for better user experience
            String successMessage = switch(userModel.getRole()) {
                case ADMIN -> 
                    "Administrator account created successfully. You now have full system access.";
                case OPERATIONS_MANAGER -> 
                    "Operations Manager account created successfully. You can now manage airport operations.";
                case GATE_MANAGER -> 
                    "Gate Manager account created successfully. Please log in to access your dashboard.";
                case AIRLINE_STAFF -> 
                    "Airline Staff account created successfully. Please log in to access your dashboard.";
                default -> 
                    "Registration successful! Please login with your credentials.";
            };
            
            // Add success message to be displayed after redirect
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, successMessage);
            
            // Redirect to login page so user can immediately log in
            return LOGIN_REDIRECT;
        } else {
            // Log registration failure
            logger.warn("Registration failed: Username already exists");
            
            // Add error message to be displayed after redirect
            redirectAttributes.addFlashAttribute(ERROR_ATTR,
                "Username already exists. Please choose a different username.");
            
            // Redirect back to registration form to try again
            return REGISTER_REDIRECT;
        }
    }
}
