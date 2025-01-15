package com.gcu.topic2_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")  // This handles the root URL
public class HomeController {
    
    /**
     * Handles requests to the root URL and displays the welcome page.
     * This method will be called when someone visits the base URL (localhost:8080/)
     *
     * @param model The Model object to add attributes to for the view
     * @return The name of the view template to render (home.html)
     */
    @GetMapping
    public String home(Model model) {
        // Add the welcome message to the model
        model.addAttribute("message", "Welcome to CST-339 Topic 2 Activity");
        return "home";  // This will look for home.html in templates directory
    }
}