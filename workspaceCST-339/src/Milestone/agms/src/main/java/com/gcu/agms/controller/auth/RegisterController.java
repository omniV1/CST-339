package com.gcu.agms.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
 * - GET /register: Displays the registration page.
 * - POST /doRegister: Processes the registration form submission.
 * 
 * Methods:
 * - showRegisterPage(Model model): Initializes and displays the registration form.
 * - processRegistration(UserModel userModel, BindingResult bindingResult, RedirectAttributes redirectAttributes):
 *   Validates the registration form, checks authorization codes for certain roles, and attempts to register the user.
 * 
 * Validation:
 * - Ensures that the authorization code is valid for roles such as ADMIN and OPERATIONS_MANAGER.
 * - Checks for validation errors in the UserModel.
 * 
 * Role Handling:
 * - Sets a default role of PUBLIC if no role is selected.
 * - Provides role-specific success messages upon successful registration.
 * 
 * Error Handling:
 * - Handles validation errors and displays appropriate messages.
 * - Checks for duplicate usernames and provides feedback if the username already exists.
 * 
 * Redirects:
 * - Redirects to the login page upon successful registration.
 * - Redirects back to the registration page with error messages if registration fails.
 */
@Controller
public class RegisterController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthorizationCodeService authCodeService;
    
    /**
     * Displays the registration page and initializes a new UserModel if needed.
     * This method handles the initial GET request to show the registration form.
     */
    @GetMapping("/register")
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
     */
    @PostMapping("/doRegister")
    public String processRegistration(
            @Valid @ModelAttribute("userModel") UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("Processing registration request for user: " + userModel.getUsername());
        
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
            System.out.println("Validation errors found:");
            bindingResult.getAllErrors().forEach(error -> 
                System.out.println(error.getDefaultMessage())
            );
            return "register";
        }
        
        // Set a default role if none was selected
        if (userModel.getRole() == null) {
            userModel.setRole(UserRole.PUBLIC);
        }
        
        System.out.println("Role set to: " + userModel.getRole());
        System.out.println("Attempting to register user...");
        
        // Attempt to register the user
        boolean registrationSuccess = userService.registerUser(userModel);
        
        if (registrationSuccess) {
            System.out.println("Registration successful for user: " + userModel.getUsername());
            
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
            return "redirect:/login";
        } else {
            System.out.println("Registration failed: Username already exists");
            redirectAttributes.addFlashAttribute("error",
                "Username already exists. Please choose a different username.");
            return "redirect:/register";
        }
    }
}