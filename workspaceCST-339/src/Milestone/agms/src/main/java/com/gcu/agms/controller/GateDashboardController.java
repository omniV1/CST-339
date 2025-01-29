package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * GateDashboardController is responsible for handling HTTP requests related to the gate management dashboard.
 * It maps requests to "/gates" and provides methods to display the dashboard.
 * 
 * Annotations:
 * @Controller - Indicates that this class serves as a controller in the Spring MVC framework.
 * @RequestMapping("/gates") - Maps HTTP requests to /gates to this controller.
 */
@Controller
@RequestMapping("/gates")
public class GateDashboardController {
    
    
    /** 
     * @param model
     * @return String
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Gate Management Dashboard - AGMS");
        return "dashboard/gate";  
    }
}