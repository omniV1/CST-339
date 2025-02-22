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
    
    // View constants
    private static final String PAGE_TITLE_ATTR = "pageTitle";
    private static final String USER_FORM_VIEW = "admin/user-form";
    private static final String DASHBOARD_VIEW = "dashboard/admin";
    private static final String DASHBOARD_REDIRECT = "redirect:/admin/dashboard";
    private static final String GATES_VIEW = "admin/gates";
    private static final String GATE_FORM_VIEW = "admin/gate-form";
    private static final String USERS_VIEW = "admin/users";
    private static final String SYSTEM_HEALTH_VIEW = "admin/system-health";
    
    // Model attribute constants
    private static final String USER_MODEL_ATTR = "userModel";
    private static final String GATE_MODEL_ATTR = "gateModel";
    private static final String USERS_ATTR = "users";
    private static final String GATES_ATTR = "gates";
    private static final String GATE_STATS_ATTR = "gateStats";
    
    // Flash attribute constants
    private static final String SUCCESS_ATTR = "success";
    private static final String ERROR_ATTR = "error";

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
        
        model.addAttribute(PAGE_TITLE_ATTR, "Admin Dashboard - AGMS");
        
        // Add user statistics
        model.addAttribute(USERS_ATTR, userService.getAllUsers());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        
        // Add gate operations statistics
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute(GATE_STATS_ATTR, gateOperationsService.getStatistics());
        
        // Add gate management information
        model.addAttribute(GATES_ATTR, gateManagementService.getAllGates());
        model.addAttribute(GATE_MODEL_ATTR, new GateModel());
        
        // Terminal-specific gate information
        for (int i = 1; i <= 4; i++) {
            model.addAttribute("terminal" + i + "Gates", 
                gateManagementService.getGatesByTerminal(String.valueOf(i)));
        }
        
        logger.info("Admin dashboard loaded successfully");
        return DASHBOARD_VIEW;
    }
    
    /**
     * Displays the system-wide gate management interface.
     * Provides administrators with full control over gate configuration and management.
     */
    @GetMapping("/gates")
    public String showGateManagement(Model model) {
        logger.info("Loading gate management view");
        model.addAttribute(PAGE_TITLE_ATTR, "Gate Management - AGMS");
        model.addAttribute(GATES_ATTR, gateManagementService.getAllGates());
        return GATES_VIEW;
    }
    
    /**
     * Displays the form for creating a new gate.
     * This provides the admin interface for adding new gates to the system.
     */
    @GetMapping("/gates/create")
    public String showCreateGateForm(Model model) {
        logger.info("Displaying gate creation form");
        model.addAttribute(PAGE_TITLE_ATTR, "Create New Gate - AGMS");
        model.addAttribute(GATE_MODEL_ATTR, new GateModel());
        return GATE_FORM_VIEW;
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
            return GATE_FORM_VIEW;
        }
        
        boolean created = gateManagementService.createGate(gateModel);
        if (created) {
            logger.info("Gate created successfully: {}", gateModel.getGateId());
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "Gate " + gateModel.getGateId() + " created successfully");
            return "redirect:/admin/gates";  // Make sure this returns a redirect
        } else {
            logger.warn("Gate creation failed - ID already exists: {}", gateModel.getGateId());
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Gate with ID " + gateModel.getGateId() + " already exists");
            return "redirect:/admin/gates";
        }
    }
    
    /**
     * Displays the form for adding a new user.
     * This provides the admin interface for adding new users to the system.
     */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute(USER_MODEL_ATTR, new UserModel());
        model.addAttribute(PAGE_TITLE_ATTR, "Add New User - AGMS");
        return USER_FORM_VIEW;
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
            return USER_FORM_VIEW;
        }

        try {
            userService.registerUser(userModel);
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "User " + userModel.getUsername() + " created successfully");
            return DASHBOARD_REDIRECT;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Failed to create user: " + e.getMessage());
            return DASHBOARD_REDIRECT;
        }
    }
    
    /**
     * Displays the user management interface.
     * Provides administrators with tools to manage system users.
     */
    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute(USERS_ATTR, userService.getAllUsers());
        model.addAttribute(PAGE_TITLE_ATTR, "User Management - AGMS");
        return USERS_VIEW;
    }
    
    /**
     * Displays system health and monitoring information.
     * Provides detailed system status and performance metrics.
     */
    @GetMapping("/system-health")
    public String showSystemHealth(Model model) {
        logger.info("Loading system health view");
        model.addAttribute(PAGE_TITLE_ATTR, "System Health - AGMS");
        
        // Add gate statistics for system health overview
        model.addAttribute(GATE_STATS_ATTR, gateOperationsService.getStatistics());
        
        return SYSTEM_HEALTH_VIEW;
    }

    /**
     * Handles the deletion of a user.
     * Administrators can delete users from the system.
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Processing user deletion request for ID: {}", id);
        
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("User deleted successfully: {}", id);
                redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "User deleted successfully");
            } else {
                logger.warn("User not found for deletion: {}", id);
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "User not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Error deleting user: " + e.getMessage());
        }
        
        return DASHBOARD_REDIRECT;
    }

    /**
     * Displays the form for editing a user.
     * This provides the admin interface for editing user details.
     */
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for user ID: {}", id);
        
        UserModel user = userService.getUserById(id);
        if (user == null) {
            return DASHBOARD_REDIRECT;
        }
        
        model.addAttribute(USER_MODEL_ATTR, user);
        model.addAttribute(PAGE_TITLE_ATTR, "Edit User - AGMS");
        return USER_FORM_VIEW;
    }

    /**
     * Handles the update of a user's details.
     * Administrators can update user information.
     */
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, 
                           @Valid UserModel userModel,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        logger.info("Processing user update request for ID: {}", id);
        
        if (result.hasErrors()) {
            return USER_FORM_VIEW;
        }

        try {
            userModel.setId(id);
            userService.updateUser(userModel);
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "User " + userModel.getUsername() + " updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user", e);
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Failed to update user: " + e.getMessage());
        }
        
        return DASHBOARD_REDIRECT;
    }
}