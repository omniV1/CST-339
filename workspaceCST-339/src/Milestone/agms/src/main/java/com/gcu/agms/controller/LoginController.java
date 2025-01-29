package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.LoginModel;
import com.gcu.agms.model.UserModel;
import com.gcu.agms.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Handles the display of the login form
     * This method is called when users navigate to the login page
     */
    @GetMapping("/login")
    public String displayLogin(Model model) {
        // Initialize a new login form if one doesn't exist in the model
        if (!model.containsAttribute("loginModel")) {
            model.addAttribute("loginModel", new LoginModel());
        }
        model.addAttribute("pageTitle", "Login - AGMS");
        return "login";
    }
    
    /**
     * Processes the login form submission
     * Handles authentication and role-based routing
     */
    @PostMapping("/doLogin")
    public String doLogin(@Valid LoginModel loginModel,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        
        System.out.println("Login attempt received:");
        System.out.println("Username: " + loginModel.getUsername());
        
        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors found");
            bindingResult.getAllErrors().forEach(error -> 
                System.out.println(error.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute("error", "Please fill in all required fields");
            return "redirect:/login";
        }
        
        try {
            System.out.println("Looking up user in service...");
            UserModel user = userService.findByUsername(loginModel.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("User found, attempting authentication...");
            if (userService.authenticate(loginModel.getUsername(), loginModel.getPassword())) {
                System.out.println("Authentication successful");
                session.setAttribute("user", user);
                session.setAttribute("userRole", user.getRole().name());
                
                String redirectUrl = switch(user.getRole()) {
                    case ADMIN -> "/admin/dashboard";
                    case OPERATIONS_MANAGER -> "/operations/dashboard";
                    case GATE_MANAGER -> "/gates/dashboard";
                    case AIRLINE_STAFF -> "/airline/dashboard";
                    case PUBLIC -> "/dashboard";
                };
                System.out.println("Redirecting to: " + redirectUrl);
                return "redirect:" + redirectUrl;
            }
            
            System.out.println("Authentication failed");
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        } catch (RuntimeException e) {
            System.out.println("Exception during login: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }
    
    /**
     * Handles user logout
     * Clears the session and redirects to login page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Clear all session data
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have been successfully logged out");
        return "redirect:/login";
    }
}