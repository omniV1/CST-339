package com.gcu.agms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * AboutController is a Spring MVC controller that handles HTTP GET requests
 * for the "/about" and "/contact" URLs. It provides information about the
 * AGMS application and contact details.
 */
@Controller
@Tag(name = "Core", description = "Core application endpoints")
public class AboutController {
    
    /**
     * Provides information about the AGMS application
     */
    @GetMapping("/about")
    @Operation(
        summary = "Display about page",
        description = "Shows information about the AGMS application, its purpose, and features"
    )
    public String showAboutPage(Model model) {
        model.addAttribute("pageTitle", "About - AGMS");
        model.addAttribute("applicationName", "Airport Gate Management System (AGMS)");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("description", "A comprehensive system for managing airport gates, flights, and operations");
        model.addAttribute("features", new String[]{
            "Flight Operations Management",
            "Gate Assignment Management",
            "Aircraft Maintenance Tracking",
            "Real-time Dashboard",
            "User Role Management"
        });
        return "about";
    }
}
