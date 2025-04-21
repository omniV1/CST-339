package com.gcu.agms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Main controller for handling homepage and general navigation
 * This controller manages the root URI and main application pages
 */
/**
 * HomeController is a Spring MVC controller that handles HTTP requests for the home and about pages.
 * It provides methods to display the main homepage and the about page of the Airport Gate Management System (AGMS).
 * 
 * Methods:
 * - showHomePage(Model model): Handles requests to the root URI and displays the main homepage.
 * - showAboutPage(Model model): Handles requests to the about page.
 * 
 * Annotations:
 * - @Controller: Indicates that this class serves as a controller in a Spring MVC application.
 * - @GetMapping: Maps HTTP GET requests to specific handler methods.
 * 
 * Each method adds attributes to the model to pass data to the view templates.
 */
@Controller
@Tag(name = "Core", description = "Core application endpoints")
public class HomeController {
    
    @GetMapping(value = {"/", "/index"})
    @Operation(
        summary = "Display home page",
        description = "Shows the welcome message and basic information about the AGMS application"
    )
    public String showHomePage(Model model) {
        model.addAttribute("pageTitle", "AGMS - Airport Gate Management System");
        model.addAttribute("welcomeMessage", "Welcome to the Airport Gate Management System");
        return "home";
    }
}