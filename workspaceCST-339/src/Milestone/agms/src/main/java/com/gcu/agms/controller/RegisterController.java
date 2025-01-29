package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gcu.agms.model.UserModel;
import com.gcu.agms.model.UserRole;
import com.gcu.agms.service.UserService;
import com.gcu.agms.service.AuthorizationCodeService;
import jakarta.validation.Valid;

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