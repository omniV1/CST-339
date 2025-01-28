package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gates")
public class GateDashboardController {
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Gate Management Dashboard - AGMS");
        return "dashboard/gates";
    }
}