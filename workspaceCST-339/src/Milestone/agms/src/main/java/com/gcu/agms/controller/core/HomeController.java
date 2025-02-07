package com.gcu.agms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
public class HomeController {
    
    /**
     * Handles requests to the root URI and displays the main homepage
     * @param model The Spring MVC model for passing data to the view
     * @return The name of the view template to render
     */
    @GetMapping("/")
    public String showHomePage(Model model) {
        // Add page title to the model
        model.addAttribute("pageTitle", "AGMS - Airport Gate Management System");
        // Add welcome message
        model.addAttribute("welcomeMessage", "Welcome to the Airport Gate Management System");
        return "home";
    }

    /**
     * Handles requests to the about page
     * @param model The Spring MVC model for passing data to the view
     * @return The name of the view template to render
     */
    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("pageTitle", "About AGMS");
        return "about";
    }
}