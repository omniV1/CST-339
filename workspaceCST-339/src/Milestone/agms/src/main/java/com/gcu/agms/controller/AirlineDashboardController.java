package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/airline")
public class AirlineDashboardController {
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Airline Staff Dashboard - AGMS");
        return "dashboard/airline";
    }
}