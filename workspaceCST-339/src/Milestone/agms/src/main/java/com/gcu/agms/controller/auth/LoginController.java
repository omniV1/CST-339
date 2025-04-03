package com.gcu.agms.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gcu.agms.model.auth.LoginModel;

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
     * Handles HTTP GET requests to /login endpoint.
     * Displays the login page with an empty login form.
     * 
     * @param model The Spring MVC Model object used to pass attributes to the view
     * @return The logical view name "login" which is resolved to login.html template
     */
    @GetMapping("/login") // Handle GET /login directly
    public String displayLogin(Model model) {
        // Add an empty LoginModel to the model if not already present
        // This is used to bind form data when the user submits the form
        if (!model.containsAttribute(LOGIN_MODEL_ATTR)) {
            model.addAttribute(LOGIN_MODEL_ATTR, new LoginModel());
        }
        
        // Set the page title attribute for the view
        model.addAttribute(PAGE_TITLE_ATTR, "Login - AGMS");
        
        // Log that we're displaying the login page for debugging purposes
        logger.debug("Displaying login page.");
        
        // Return the logical view name to be resolved to the actual template
        return "login";
    }
    
    /**
     * Previously handled login POST requests, now removed and replaced by Spring Security.
     * 
     * This method used to:
     * 1. Validate login form input
     * 2. Authenticate credentials against the database
     * 3. Set up user session on successful login
     * 4. Redirect to different dashboards based on user role
     * 
     * Now Spring Security handles all these operations through its filter chain.
     */
    /* 
    @PostMapping("/login") // Maps to /auth/login 
    public String doLogin(@Valid LoginModel loginModel,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        
        logger.info("Processing login request for user: {}", loginModel.getUsername());
        
        // Validate form data
        if (bindingResult.hasErrors()) {
            logger.warn("Login form validation failed");
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please fill in all required fields");
            return LOGIN_REDIRECT;
        }
        
        // Validate credential format
        if (!loginService.validateCredentials(loginModel)) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Invalid username or password format");
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
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "Invalid username or password");
                return LOGIN_REDIRECT;
            });
    }
    */
    
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
