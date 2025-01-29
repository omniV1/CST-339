package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

/**
 * DashboardController handles routing to different dashboards based on the user's role.
 * 
 * <p>This controller maps the "/dashboard" endpoint and redirects users to their respective
 * dashboards based on their role stored in the session. If the user's role is not found in
 * the session, they are redirected to the login page.</p>
 * 
 * <p>Role-based routing is handled using a modern switch expression for cleaner and more
 * readable code.</p>
 * 
 * <p>Roles and their corresponding redirects:
 * <ul>
 *   <li>ADMIN - Redirects to "/admin/dashboard"</li>
 *   <li>OPERATIONS_MANAGER - Redirects to "/operations/dashboard"</li>
 *   <li>GATE_MANAGER - Redirects to "/gates/dashboard"</li>
 *   <li>AIRLINE_STAFF - Redirects to "/airline/dashboard"</li>
 *   <li>Default - Redirects to the home page ("/")</li>
 * </ul>
 * </p>
 * 
 * @param model the model to be used in the view
 * @param session the HTTP session containing user attributes
 * @return the redirect URL based on the user's role
 */
@Controller
public class DashboardController {
    
    
    /** 
     * @param model
     * @param session
     * @return String
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Get the user's role from the session
        String userRole = (String) session.getAttribute("userRole");
        
        // Using modern switch expression for cleaner role-based routing
        return userRole == null ? "redirect:/login" : switch(userRole) {
            case "ADMIN" -> "redirect:/admin/dashboard";
            case "OPERATIONS_MANAGER" -> "redirect:/operations/dashboard";
            case "GATE_MANAGER" -> "redirect:/gates/dashboard";
            case "AIRLINE_STAFF" -> "redirect:/airline/dashboard";
            default -> "redirect:/";
        };
    }
}