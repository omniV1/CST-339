package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcu.agms.model.auth.LoginModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

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
// @RequestMapping({"/auth"}) // Removed - Now mapped directly
public class LoginController {
    /**
     * Logger instance for this class, used to log different levels of information
     * for debugging, error tracking, and application monitoring purposes.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    // Previous code that was refactored out when moving to Spring Security
    // private static final String LOGIN_REDIRECT = "redirect:/auth/login"; // No longer needed
    // private static final String ERROR_ATTR = "error"; // No longer needed here
    // private static final String SUCCESS_ATTR = "successMessage"; // No longer needed here
    
    /**
     * Constant for the page title attribute name in the model
     * Used to set the title of the login page displayed in browser
     */
    private static final String PAGE_TITLE_ATTR = "pageTitle";
    
    /**
     * Constant for the login model attribute name in the model
     * Used to bind form data from the view to the LoginModel object
     */
    private static final String LOGIN_MODEL_ATTR = "loginModel"; // Kept for consistency, though not strictly needed by Spring Security form
    
    // LoginService dependency injection was removed when switching to Spring Security
    // private final LoginService loginService; 
    
    /**
     * Constructor (can be removed if LoginService is not injected).
     * No longer needed since authentication is handled by Spring Security.
     */
    // public LoginController(LoginService loginService) {
    //    this.loginService = loginService;
    // }
    
    /**
     * Returns login page information and form structure
     */
    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Get login page information",
        description = "Returns the login page structure and any required form fields"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved login page information",
            content = @Content(mediaType = "application/json")
        )
    })
    public Map<String, Object> getLoginInfo() {
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put(PAGE_TITLE_ATTR, "Login - AGMS");
        loginInfo.put("formFields", new String[]{"username", "password"});
        loginInfo.put("method", "POST");
        loginInfo.put("action", "/login");
        return loginInfo;
    }
    
    /**
     * Processes login requests (handled by Spring Security)
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Process login request",
        description = "Authenticates user credentials and establishes a session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(mediaType = "application/json")
        )
    })
    public Map<String, Object> processLogin(
            @Parameter(description = "Login credentials", required = true)
            @RequestBody LoginModel loginModel) {
        // This endpoint is actually handled by Spring Security
        // This is just for documentation purposes
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint is handled by Spring Security");
        return response;
    }
    
    /**
     * Previously handled logout requests, now removed and replaced by Spring Security.
     * 
     * This method used to:
     * 1. Invalidate the user's session
     * 2. Redirect to login page with a success message
     * 
     * Now Spring Security provides a /logout endpoint that handles session invalidation.
     */
    /*
    @GetMapping("/logout") // Maps to /auth/logout
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        logger.info("Processing logout request");
        session.invalidate();
        redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
            "You have been successfully logged out");
        return LOGIN_REDIRECT;
    }
    */
}
