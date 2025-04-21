package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for handling user login operations.
 * This controller is responsible for displaying the login form to users.
 * Actual authentication processing is delegated to Spring Security framework.
 * 
 * The controller follows MVC architecture pattern where:
 * - Model: LoginModel class
 * - View: login.html template
 * - Controller: This LoginController class
 */
@Controller
@Tag(name = "Authentication", description = "Authentication endpoints for user login and registration")
public class LoginController {
    /**
     * Logger instance for this class, used to log different levels of information
     * for debugging, error tracking, and application monitoring purposes.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    /**
     * Displays the login page
     */
    @GetMapping("/login")
    @Operation(
        summary = "Display login page",
        description = "Shows the login form for user authentication"
    )
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Login - AGMS");
        return "login";
    }
}
