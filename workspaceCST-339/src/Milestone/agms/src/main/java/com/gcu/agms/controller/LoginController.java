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
    
    if (bindingResult.hasErrors()) {
        redirectAttributes.addFlashAttribute("error", "Please fill in all required fields");
        return "redirect:/login";
    }
    
    try {
        UserModel user = userService.findByUsername(loginModel.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (userService.authenticate(loginModel.getUsername(), loginModel.getPassword())) {
            // Store base user information
            session.setAttribute("user", user);
            
            // Using switch expression for role assignment and routing
            String username = loginModel.getUsername().toLowerCase();
            String redirectUrl = switch(username) {
                case "admin" -> {
                    session.setAttribute("userRole", "ADMIN");
                    yield "redirect:/admin/dashboard";
                }
                case "operations" -> {
                    session.setAttribute("userRole", "OPERATIONS_MANAGER");
                    yield "redirect:/operations/dashboard";
                }
                case "gate" -> {
                    session.setAttribute("userRole", "GATE_MANAGER");
                    yield "redirect:/gates/dashboard";
                }
                case "airline" -> {
                    session.setAttribute("userRole", "AIRLINE_STAFF");
                    yield "redirect:/airline/dashboard";
                }
                default -> {
                    session.setAttribute("userRole", "PUBLIC");
                    yield "redirect:/dashboard";
                }
            };
            
            return redirectUrl;
        }
        
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    } catch (RuntimeException e) {
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