package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.agms.service.GateOperationsService;

import jakarta.servlet.http.HttpSession;

/**
 * OperationsDashboardController is responsible for handling requests related to the operations dashboard.
 * It provides functionality to display the operations dashboard to users with the role of "OPERATIONS_MANAGER".
 * 
 * Annotations:
 * @Controller - Indicates that this class serves the role of a controller in the Spring MVC framework.
 * @RequestMapping("/operations") - Maps HTTP requests to handler methods of MVC and REST controllers.
 * 
 * Dependencies:
 * @Autowired - Injects the GateOperationsService dependency.
 * 
 * Methods:
 * 
 * @GetMapping("/dashboard")
 * public String showDashboard(Model model, HttpSession session)
 * - Handles GET requests to "/operations/dashboard".
 * - Verifies the user's role and redirects to the login page if the user is not an "OPERATIONS_MANAGER".
 * - Adds the page title, gate status data, and statistics to the model.
 * - Returns the view name for the operations dashboard.
 * 
 * @param model - The Model object to pass data to the view.
 * @param session - The HttpSession object to retrieve user session attributes.
 * @return The view name for the operations dashboard or a redirect to the login page if the user is not authorized.
 */
@Controller
@RequestMapping("/operations")
public class OperationsDashboardController {
    
    @Autowired
    private GateOperationsService gateOperationsService;
    
    
    /** 
     * @param model
     * @param session
     * @return String
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify user role
        String userRole = (String) session.getAttribute("userRole");
        if (!"OPERATIONS_MANAGER".equals(userRole)) {
            return "redirect:/login";
        }
        
        // Add the page title
        model.addAttribute("pageTitle", "Operations Dashboard - AGMS");
        
        // Add gate status data and statistics
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute("statistics", gateOperationsService.getStatistics());
        
        // Return the dashboard view
        return "dashboard/operations";
    }
}