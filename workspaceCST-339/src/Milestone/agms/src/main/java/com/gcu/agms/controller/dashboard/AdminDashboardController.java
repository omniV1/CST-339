package com.gcu.agms.controller.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.auth.UserService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

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
     * Displays the form for adding a new user.
     * This provides the admin interface for adding new users to the system.
     */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("userModel", new UserModel());
        model.addAttribute("pageTitle", "Add New User - AGMS");
        return "admin/user-form";
    }

    /**
     * Handles the addition of a new user.
     * Administrators can add users with full configuration options.
     */
    @PostMapping("/users/add")
    public String addUser(@Valid UserModel userModel, 
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }

        try {
            userService.registerUser(userModel);
            redirectAttributes.addFlashAttribute("success", 
                "User " + userModel.getUsername() + " created successfully");
            return "redirect:/admin/dashboard";  // Changed from "/admin/users" to "/admin/dashboard"
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create user: " + e.getMessage());
            return "redirect:/admin/dashboard";  // Changed from "/admin/users" to "/admin/dashboard"
        }
    }
    
    /**
     * Displays the user management interface.
     * Provides administrators with tools to manage system users.
     */
    @Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    private final UserService userService;
    
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String getUserManagement(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "dashboard/admin/users";
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("pageTitle", "User Management - AGMS");
        return "admin/users";
    }
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

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Processing user deletion request for ID: {}", id);
        
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("User deleted successfully: {}", id);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully");
            } else {
                logger.warn("User not found for deletion: {}", id);
                redirectAttributes.addFlashAttribute("error", "User not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for user ID: {}", id);
        
        UserModel user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("userModel", user);
        model.addAttribute("pageTitle", "Edit User - AGMS");
        return "admin/user-form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, 
                           @Valid UserModel userModel,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        logger.info("Processing user update request for ID: {}", id);
        
        if (result.hasErrors()) {
            return "admin/user-form";
        }

        try {
            userModel.setId(id);
            userService.updateUser(userModel);
            redirectAttributes.addFlashAttribute("success", 
                "User " + userModel.getUsername() + " updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to update user: " + e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
}