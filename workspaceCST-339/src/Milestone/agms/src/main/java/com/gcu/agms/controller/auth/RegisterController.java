package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * RegisterController handles the registration process for new users.
 * It provides endpoints to display the registration form and process the form submission.
 * 
 * Dependencies:
 * - UserService: Handles user-related operations such as registration.
 * - AuthorizationCodeService: Validates authorization codes for roles requiring special permissions.
 * 
 * Endpoints:
 * - GET /auth/register: Displays the registration page.
 * - POST /auth/register: Processes the registration form submission.
 */
@Controller
@RequestMapping("/auth") // Add base mapping to match other auth controllers
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    
    private final UserService userService;
    private final AuthorizationCodeService authCodeService;
    
    @Autowired
    public RegisterController(UserService userService, AuthorizationCodeService authCodeService) {
        this.userService = userService;
        this.authCodeService = authCodeService;
    }
    
    /**
     * Displays the registration page and initializes a new UserModel if needed.
     * This method handles the initial GET request to show the registration form.
     * 
     * @param model the model to be used in the view
     * @return the registration view name
     */
    @GetMapping("/register") // Now maps to /auth/register
    public String showRegisterPage(Model model) {
        // Create a new UserModel if one doesn't exist in the model
        if (!model.containsAttribute("userModel")) {
            model.addAttribute("userModel", new UserModel());
        }
        model.addAttribute("pageTitle", "Register - AGMS");
        return "register";
    }
    
    /**
     * Processes the registration form submission.
     * This method handles validation, role authorization, and user creation.
     * 
     * @param userModel the user form data
     * @param bindingResult the result of form validation
     * @param redirectAttributes attributes for redirect scenarios
     * @return the redirect URL based on the registration result
     */
    @PostMapping("/register") // Changed from /doRegister to /register for consistency
    public String processRegistration(
            @Valid @ModelAttribute("userModel") UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Processing registration request for user: {}", userModel.getUsername());
        
        // First, check if this is an administrative role requiring authorization
        if (userModel.getRole() == UserRole.ADMIN || 
            userModel.getRole() == UserRole.OPERATIONS_MANAGER) {
            
            // Verify the authorization code matches the requested role
            if (!authCodeService.isValidAuthCode(userModel.getAuthCode(), userModel.getRole())) {
                bindingResult.rejectValue("authCode", "invalid.authCode", 
                    "Invalid authorization code for the selected role.");
                return "register";
            }
        }
        
        // Check for any validation errors from the @Valid annotation
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors found during registration");
            return "register";
        }
        
        // Set a default role if none was selected
        if (userModel.getRole() == null) {
            userModel.setRole(UserRole.PUBLIC);
        }
        
        logger.info("Role set to: {}", userModel.getRole());
        logger.info("Attempting to register user...");
        
        // Attempt to register the user
        boolean registrationSuccess = userService.registerUser(userModel);
        
        if (registrationSuccess) {
            logger.info("Registration successful for user: {}", userModel.getUsername());
            
            // Create role-specific success messages
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
            
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/auth/login"; // Update redirect path
        } else {
            logger.warn("Registration failed: Username already exists");
            redirectAttributes.addFlashAttribute("error",
                "Username already exists. Please choose a different username.");
            return "redirect:/register";
        }
    }
}