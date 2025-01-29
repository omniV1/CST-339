package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class for handling requests related to the airline staff dashboard.
 * This class maps requests to "/airline" and provides methods to handle specific
 * endpoints under this path.
 * 
 * Annotations:
 * @Controller - Indicates that this class serves as a controller in the Spring MVC framework.
 * @RequestMapping("/airline") - Maps requests with the "/airline" path to this controller.
 */
@Controller
@RequestMapping("/airline")
public class AirlineDashboardController {
    
    
    /** 
     * @param model
     * @return String
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Airline Staff Dashboard - AGMS");
        return "dashboard/airline";
    }
}