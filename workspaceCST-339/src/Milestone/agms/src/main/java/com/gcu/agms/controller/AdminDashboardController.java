package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminDashboardController is responsible for handling requests to the admin dashboard.
 * It maps to the "/admin" URL path and provides methods to display the admin dashboard page.
 * 
 * Annotations:
 * @Controller - Indicates that this class serves as a controller in the Spring MVC framework.
 * @RequestMapping("/admin") - Maps HTTP requests to handler methods of MVC and REST controllers.
 * 
 * Methods:
 * 
 * showDashboard(Model model):
 * - Handles GET requests to "/admin/dashboard".
 * - Adds the page title "Admin Dashboard - AGMS" to the model.
 * - Returns the view name "dashboard/admin" to be rendered.
 * 
 * @param model - The Model object used to pass attributes to the view.
 * @return The name of the view to be rendered.
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    
    
    /** 
     * @param model
     * @return String
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard - AGMS");
        return "dashboard/admin";
    }
}
