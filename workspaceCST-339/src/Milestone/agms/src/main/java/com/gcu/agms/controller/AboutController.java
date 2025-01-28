package com.gcu.agms.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class AboutController {
                
    // This is a controller for the about page
    // It is a simple controller that returns the about page
    // It is mapped to the /about URL
    // It is a simple controller that returns the about page
    // It is mapped to the /about URL
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
}
