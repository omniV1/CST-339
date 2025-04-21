package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.auth.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    private static final String LOGIN_REDIRECT = "redirect:/login";     // Redirect URL to login page
    private static final String USER_MODEL_ATTR = "userModel";               // Attribute name for the user model
    private static final String PAGE_TITLE_ATTR = "pageTitle";               // Attribute name for page title
    private static final String ERROR_ATTR = "error";                        // Attribute name for error messages
    private static final String SUCCESS_ATTR = "successMessage";             // Attribute name for success messages
    
    /**
     * Service dependencies injected through constructor.
     */
    private final UserService userService;
    private final AuthorizationCodeService authCodeService;
    
    /**
     * Constructor with dependency injection for required services.
     *
     * @param userService Service for user management operations
     * @param authCodeService Service for authorization code validation
     */
    public RegisterController(UserService userService, AuthorizationCodeService authCodeService) {
        this.userService = userService;
        this.authCodeService = authCodeService;
    }
    
    /**
     * Displays the registration form
     */
    @GetMapping("/register")
    @Operation(
        summary = "Display registration page",
        description = "Shows the registration form for new users"
    )
    public String showRegistrationForm(Model model) {
        model.addAttribute(PAGE_TITLE_ATTR, "Register - AGMS");
        model.addAttribute(USER_MODEL_ATTR, new UserModel());
        model.addAttribute("roles", UserRole.values());
        return REGISTER_VIEW;
    }
    
    /**
     * Processes the registration form submission
     */
    @PostMapping("/register")
    @Operation(
        summary = "Process registration",
        description = "Handles the registration form submission and creates a new user account"
    )
    public String processRegistration(
            @Valid UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.info("Processing registration request for user: {}", userModel.getUsername());
        
        // Validate authorization code for special roles
        if ((userModel.getRole() == UserRole.ADMIN || 
             userModel.getRole() == UserRole.OPERATIONS_MANAGER) &&
            !authCodeService.isValidAuthCode(userModel.getAuthCode(), userModel.getRole())) {
            
            model.addAttribute(ERROR_ATTR, "Invalid authorization code for the selected role");
            model.addAttribute(PAGE_TITLE_ATTR, "Register - AGMS");
            model.addAttribute("roles", UserRole.values());
            return REGISTER_VIEW;
        }

        // Set default role if none provided
        userModel.setRole(userModel.getRole() == null ? UserRole.PUBLIC : userModel.getRole());
        
        // Attempt registration
        if (userService.registerUser(userModel)) {
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "Registration successful! Please log in with your credentials.");
            return LOGIN_REDIRECT;
        } else {
            model.addAttribute(ERROR_ATTR, "Registration failed. Please try again.");
            model.addAttribute(PAGE_TITLE_ATTR, "Register - AGMS");
            model.addAttribute("roles", UserRole.values());
            return REGISTER_VIEW;
        }
    }
}
