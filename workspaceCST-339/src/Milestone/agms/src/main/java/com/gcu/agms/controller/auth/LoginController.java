package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.LoginModel;
import com.gcu.agms.service.auth.LoginService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller for handling user login operations.
 * Demonstrates proper use of dependency injection and separation of concerns
 * as required by Milestone 3.
 */
@Controller
@RequestMapping("/auth")  // Add base mapping
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_REDIRECT = "redirect:/auth/login";
    
    private final LoginService loginService;
    
    /**
     * Constructor injection of LoginService.
     * @param loginService Service handling login operations
     */
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    
    /**
     * Displays the login page.
     * @param model the model to be used in the view
     * @return the login view name
     */
    @GetMapping("/login") // Maps to /auth/login
    public String displayLogin(Model model) {
        if (!model.containsAttribute("loginModel")) {
            model.addAttribute("loginModel", new LoginModel());
        }
        model.addAttribute("pageTitle", "Login - AGMS");
        return "login";
    }
    
    /**
     * Processes the login request.
     * @param loginModel the login form data
     * @param bindingResult the result of form validation
     * @param session the HTTP session
     * @param redirectAttributes attributes for redirect scenarios
     * @return the redirect URL based on the authentication result
     */
    @PostMapping("/login") // Maps to /auth/login 
    public String doLogin(@Valid LoginModel loginModel,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        
        logger.info("Processing login request for user: {}", loginModel.getUsername());
        
        // Validate form data
        if (bindingResult.hasErrors()) {
            logger.warn("Login form validation failed");
            redirectAttributes.addFlashAttribute("error", "Please fill in all required fields");
            return LOGIN_REDIRECT;
        }
        
        // Validate credential format
        if (!loginService.validateCredentials(loginModel)) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password format");
            return LOGIN_REDIRECT;
        }
        
        // Attempt authentication
        return loginService.authenticate(loginModel)
            .map(user -> {
                // Set session attributes
                session.setAttribute("user", user);
                session.setAttribute("userRole", user.getRole().name());
                logger.info("User {} successfully logged in", user.getUsername());
                
                // Redirect based on role
                return switch(user.getRole()) {
                    case ADMIN -> "redirect:/admin/dashboard";
                    case OPERATIONS_MANAGER -> "redirect:/operations/dashboard";
                    case GATE_MANAGER -> "redirect:/gates/dashboard";
                    case AIRLINE_STAFF -> "redirect:/airline/dashboard";
                    case PUBLIC -> "redirect:/dashboard";
                };
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return LOGIN_REDIRECT;
            });
    }
    
    /**
     * Processes the logout request.
     * @param session the HTTP session
     * @param redirectAttributes attributes for redirect scenarios
     * @return the redirect URL to the login page
     */
    @GetMapping("/logout") // Maps to /auth/logout
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        logger.info("Processing logout request");
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", 
            "You have been successfully logged out");
        return LOGIN_REDIRECT;
    }
}