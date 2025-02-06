package com.gcu.agms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.GateModel;
import com.gcu.agms.service.GateManagementService;
import com.gcu.agms.service.GateOperationsService;
import com.gcu.agms.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller responsible for the administrator dashboard and system management functions.
 * This controller provides comprehensive system management capabilities including
 * user management, gate operations oversight, and system monitoring. According to
 * the system design, administrators have full access to all system functions.
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);
    
    private final UserService userService;
    private final GateOperationsService gateOperationsService;
    private final GateManagementService gateManagementService;
    
    /**
     * Constructor injection of required services.
     * The admin dashboard requires access to all major system services
     * to provide comprehensive management capabilities.
     */
    @Autowired
    public AdminDashboardController(
            UserService userService,
            GateOperationsService gateOperationsService,
            GateManagementService gateManagementService) {
        this.userService = userService;
        this.gateOperationsService = gateOperationsService;
        this.gateManagementService = gateManagementService;
    }
    
    /**
     * Displays the main admin dashboard with system-wide statistics and management options.
     * This method aggregates information from all services to provide a comprehensive
     * overview of system status.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to admin dashboard");
            return "redirect:/login";
        }
        
        logger.info("Loading admin dashboard");
        
        // Add page title
        model.addAttribute("pageTitle", "Admin Dashboard - AGMS");
        
        // Add user statistics
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        
        // Add gate operations statistics
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute("gateStats", gateOperationsService.getStatistics());
        
        // Add gate management information
        model.addAttribute("gates", gateManagementService.getAllGates());

        model.addAttribute("gateModel", new GateModel());
        
        // Terminal-specific gate information
        for (int i = 1; i <= 4; i++) {
            model.addAttribute("terminal" + i + "Gates", 
                gateManagementService.getGatesByTerminal(String.valueOf(i)));
        }
        
        logger.info("Admin dashboard loaded successfully");
        return "dashboard/admin";
    }
    
    /**
     * Displays the system-wide gate management interface.
     * Provides administrators with full control over gate configuration and management.
     */
    @GetMapping("/gates")
    public String showGateManagement(Model model) {
        logger.info("Loading gate management view");
        model.addAttribute("pageTitle", "Gate Management - AGMS");
        model.addAttribute("gates", gateManagementService.getAllGates());
        return "admin/gates";
    }
    
    /**
     * Displays the form for creating a new gate.
     * This provides the admin interface for adding new gates to the system.
     */
    @GetMapping("/gates/create")
    public String showCreateGateForm(Model model) {
        logger.info("Displaying gate creation form");
        model.addAttribute("pageTitle", "Create New Gate - AGMS");
        model.addAttribute("gateModel", new GateModel());
        return "admin/gate-form";
    }
    
    /**
     * Handles the creation of a new gate.
     * Administrators can create gates with full configuration options.
     */
    @PostMapping("/gates/create")
    public String createGate(@Valid GateModel gateModel, 
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        logger.info("Processing gate creation request for ID: {}", gateModel.getGateId());
        
        if (result.hasErrors()) {
            logger.warn("Gate creation validation failed");
            return "admin/gate-form";
        }
        
        boolean created = gateManagementService.createGate(gateModel);
        if (created) {
            logger.info("Gate created successfully: {}", gateModel.getGateId());
            redirectAttributes.addFlashAttribute("success", 
                "Gate " + gateModel.getGateId() + " created successfully");
        } else {
            logger.warn("Gate creation failed - ID already exists: {}", gateModel.getGateId());
            redirectAttributes.addFlashAttribute("error", 
                "Gate with ID " + gateModel.getGateId() + " already exists");
        }
        
        return "redirect:/admin/gates";
    }
    
    /**
     * Displays the user management interface.
     * Provides administrators with tools to manage system users.
     */
    @GetMapping("/users")
    public String showUserManagement(Model model) {
        logger.info("Loading user management view");
        model.addAttribute("pageTitle", "User Management - AGMS");
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
    
    /**
     * Displays system health and monitoring information.
     * Provides detailed system status and performance metrics.
     */
    @GetMapping("/system-health")
    public String showSystemHealth(Model model) {
        logger.info("Loading system health view");
        model.addAttribute("pageTitle", "System Health - AGMS");
        
        // Add gate statistics for system health overview
        model.addAttribute("gateStats", gateOperationsService.getStatistics());
        
        return "admin/system-health";
    }
}