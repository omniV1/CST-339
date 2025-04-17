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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Administrator Dashboard Controller for the Airport Gate Management System.
 * 
 * This controller manages all administrative functions of the AGMS application and
 * is accessible only to users with the ADMIN role. It provides comprehensive system
 * management capabilities including:
 * 
 * 1. User Management
 *    - Creating, updating, and deleting system users
 *    - Managing user roles and permissions
 *    - Viewing all users in the system
 * 
 * 2. Gate Management
 *    - Creating and configuring airport gates
 *    - Monitoring gate status across all terminals
 *    - Viewing terminal-specific gate information
 * 
 * 3. System Monitoring
 *    - Viewing system health metrics
 *    - Monitoring operational statistics
 *    - Accessing logs and system performance data
 * 
 * The controller follows RESTful URL patterns with the base path "/admin" and
 * implements POST-REDIRECT-GET pattern for form submissions to prevent duplicate
 * submissions and ensure proper error/success message handling.
 * 
 * This is one of the role-specific dashboard controllers in the system, with access
 * restricted by Spring Security configuration to users with ADMIN role.
 */
@Controller
@RequestMapping("/admin")
@Tag(name = "Admin Dashboard", description = "Administrative endpoints for managing users, gates, and system configuration")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {
    /**
     * Logger for this controller class.
     * Used to record administrative actions for audit trails and troubleshooting.
     */
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);
    
    /**
     * View name and model attribute constants.
     * Centralizing these as constants ensures consistency across methods
     * and makes refactoring view names easier.
     */
    // View template paths
    private static final String PAGE_TITLE_ATTR = "pageTitle";
    private static final String USER_FORM_VIEW = "admin/user-form";
    private static final String DASHBOARD_VIEW = "dashboard/admin";
    private static final String DASHBOARD_REDIRECT = "redirect:/admin/dashboard";
    private static final String GATES_VIEW = "admin/gates";
    private static final String GATE_FORM_VIEW = "admin/gate-form";
    private static final String USERS_VIEW = "admin/users";
    private static final String SYSTEM_HEALTH_VIEW = "admin/system-health";
    
    /**
     * Model attribute name constants.
     * These define the keys used to pass data to the view templates.
     */
    private static final String USER_MODEL_ATTR = "userModel";
    private static final String GATE_MODEL_ATTR = "gateModel";
    private static final String USERS_ATTR = "users";
    private static final String GATES_ATTR = "gates";
    private static final String GATE_STATS_ATTR = "gateStats";
    
    /**
     * Flash attribute constants for success and error messages.
     * Used in the POST-REDIRECT-GET pattern to display notifications after redirects.
     */
    private static final String SUCCESS_ATTR = "success";
    private static final String ERROR_ATTR = "error";

    /**
     * Service dependencies injected through constructor.
     * The admin dashboard requires access to multiple services to
     * provide comprehensive system management.
     */
    private final UserService userService;
    private final GateOperationsService gateOperationsService;
    private final GateManagementService gateManagementService;
    
    /**
     * Constructor injection of required services.
     * 
     * The admin dashboard requires access to all major system services:
     * - UserService: For managing system users and their permissions
     * - GateOperationsService: For monitoring gate status and operations
     * - GateManagementService: For managing gate configurations
     * 
     * Constructor injection is preferred over field injection as it:
     * - Makes dependencies explicit and testable
     * - Ensures the controller cannot be instantiated without its required services
     * - Supports immutability (final fields)
     * 
     * @param userService Service for user management operations
     * @param gateOperationsService Service for gate status and operational data
     * @param gateManagementService Service for gate configuration management
     */
    public AdminDashboardController(
            UserService userService,
            GateOperationsService gateOperationsService,
            GateManagementService gateManagementService) {
        this.userService = userService;
        this.gateOperationsService = gateOperationsService;
        this.gateManagementService = gateManagementService;
    }
    
    @Operation(
        summary = "Get admin dashboard",
        description = "Displays the main administrative dashboard with comprehensive system overview including " +
                     "user statistics, gate statuses, operational metrics, and terminal-specific information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        logger.info("Loading admin dashboard");
        
        // Set page title for browser tab
        model.addAttribute(PAGE_TITLE_ATTR, "Admin Dashboard - AGMS");
        
        // Add user statistics from UserService
        model.addAttribute(USERS_ATTR, userService.getAllUsers());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        
        // Add gate operations statistics from GateOperationsService
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute(GATE_STATS_ATTR, gateOperationsService.getStatistics());
        
        // Add gate management information from GateManagementService
        model.addAttribute(GATES_ATTR, gateManagementService.getAllGates());
        model.addAttribute(GATE_MODEL_ATTR, new GateModel());
        
        // Add terminal-specific gate information for all 4 terminals
        // This allows the dashboard to show gates grouped by terminal
        for (int i = 1; i <= 4; i++) {
            model.addAttribute("terminal" + i + "Gates", 
                gateManagementService.getGatesByTerminal(String.valueOf(i)));
        }
        
        // Add authorization code management access flag
        // Only admins have access to manage authorization codes
        model.addAttribute("hasAuthCodeManagement", true);
        
        logger.info("Admin dashboard loaded successfully");
        return DASHBOARD_VIEW;
    }
    
    @Operation(
        summary = "Get gate management view",
        description = "Displays the system-wide gate management interface showing all gates across terminals, " +
                     "their current status, and management options"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate management view loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/gates")
    public String showGateManagement(Model model) {
        logger.info("Loading gate management view");
        
        // Set page title and add all gates to the model
        model.addAttribute(PAGE_TITLE_ATTR, "Gate Management - AGMS");
        model.addAttribute(GATES_ATTR, gateManagementService.getAllGates());
        
        return GATES_VIEW;
    }
    
    @Operation(
        summary = "Show gate creation form",
        description = "Displays the form for creating a new gate with fields for gate ID, terminal information, " +
                     "type, size configurations, and equipment settings"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate creation form loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/gates/create")
    public String showCreateGateForm(Model model) {
        logger.info("Displaying gate creation form");
        
        // Set page title and add empty gate model for form binding
        model.addAttribute(PAGE_TITLE_ATTR, "Create New Gate - AGMS");
        model.addAttribute(GATE_MODEL_ATTR, new GateModel());
        
        return GATE_FORM_VIEW;
    }
    
    @Operation(
        summary = "Create new gate",
        description = "Processes the gate creation form submission and creates a new gate in the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Gate created successfully - Redirected to gate list"),
        @ApiResponse(responseCode = "400", description = "Invalid gate data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "409", description = "Gate ID already exists")
    })
    @PostMapping("/gates/create")
    public String createGate(
            @Parameter(description = "Gate details", required = true)
            @Valid GateModel gateModel, 
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        logger.info("Processing gate creation request for ID: {}", gateModel.getGateId());
        
        // If validation errors exist, return to form with error messages
        if (result.hasErrors()) {
            logger.warn("Gate creation validation failed");
            return GATE_FORM_VIEW;
        }
        
        // Attempt to create the gate
        boolean created = gateManagementService.createGate(gateModel);
        
        // Handle the result with appropriate messages
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
    
    @Operation(
        summary = "Show user creation form",
        description = "Displays the form for adding a new user with fields for credentials, " +
                     "personal information, and role settings"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User creation form loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        // Set page title and add empty user model for form binding
        model.addAttribute(USER_MODEL_ATTR, new UserModel());
        model.addAttribute(PAGE_TITLE_ATTR, "Add New User - AGMS");
        
        return USER_FORM_VIEW;
    }

    @Operation(
        summary = "Create new user",
        description = "Processes the user creation form submission and registers a new user in the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "User created successfully - Redirected to dashboard"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/users/add")
    public String addUser(
            @Parameter(description = "User details", required = true)
            @Valid UserModel userModel, 
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        // If validation errors exist, return to form with error messages
        if (result.hasErrors()) {
            return USER_FORM_VIEW;
        }

        try {
            // Attempt to register the user
            userService.registerUser(userModel);
            
            // Add success message and redirect to dashboard
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "User " + userModel.getUsername() + " created successfully");
            return DASHBOARD_REDIRECT;
        } catch (Exception e) {
            // Handle errors with appropriate message
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Failed to create user: " + e.getMessage());
            return DASHBOARD_REDIRECT;
        }
    }
    
    @Operation(
        summary = "Get user management view",
        description = "Displays the user management interface showing all system users, their roles, " +
                     "status, and management options"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User management view loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/users")
    public String showUsers(Model model) {
        // Set page title and add all users to the model
        model.addAttribute(USERS_ATTR, userService.getAllUsers());
        model.addAttribute(PAGE_TITLE_ATTR, "User Management - AGMS");
        
        return USERS_VIEW;
    }
    
    @Operation(
        summary = "Get system health view",
        description = "Displays system health and monitoring information including performance metrics, " +
                     "database status, and operational statistics"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "System health view loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/system-health")
    public String showSystemHealth(Model model) {
        logger.info("Loading system health view");
        
        // Set page title for the system health view
        model.addAttribute(PAGE_TITLE_ATTR, "System Health - AGMS");
        
        // Add gate statistics for system health overview
        // These statistics provide insight into system operational status
        model.addAttribute(GATE_STATS_ATTR, gateOperationsService.getStatistics());
        
        return SYSTEM_HEALTH_VIEW;
    }

    @Operation(
        summary = "Delete user",
        description = "Removes a user from the system based on their ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "User deleted successfully - Redirected to dashboard"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/users/delete/{id}")
    public String deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id, 
            RedirectAttributes redirectAttributes) {
        logger.info("Processing user deletion request for ID: {}", id);
        
        try {
            // Attempt to delete the user
            boolean deleted = userService.deleteUser(id);
            
            // Handle the result with appropriate messages
            if (deleted) {
                logger.info("User deleted successfully: {}", id);
                redirectAttributes.addFlashAttribute(SUCCESS_ATTR, "User deleted successfully");
            } else {
                logger.warn("User not found for deletion: {}", id);
                redirectAttributes.addFlashAttribute(ERROR_ATTR, "User not found");
            }
        } catch (Exception e) {
            // Handle any exceptions during deletion
            logger.error("Error deleting user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Error deleting user: " + e.getMessage());
        }
        
        return DASHBOARD_REDIRECT;
    }

    @Operation(
        summary = "Show user edit form",
        description = "Displays the form for editing an existing user's information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User edit form loaded successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id, 
            Model model) {
        logger.info("Showing edit form for user ID: {}", id);
        
        // Retrieve the user by ID
        UserModel user = userService.getUserById(id);
        
        // If user not found, redirect to dashboard
        if (user == null) {
            return DASHBOARD_REDIRECT;
        }
        
        // Set up the model with user data and page title
        model.addAttribute(USER_MODEL_ATTR, user);
        model.addAttribute(PAGE_TITLE_ATTR, "Edit User - AGMS");
        
        return USER_FORM_VIEW;
    }

    @Operation(
        summary = "Update user",
        description = "Processes the user update form submission and updates the user's information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "User updated successfully - Redirected to dashboard"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/users/update/{id}")
    public String updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Updated user details", required = true)
            @Valid UserModel userModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        logger.info("Processing user update request for ID: {}", id);
        
        // If validation errors exist, return to form with error messages
        if (result.hasErrors()) {
            return USER_FORM_VIEW;
        }

        try {
            // Set the ID from path variable and update the user
            userModel.setId(id);
            userService.updateUser(userModel);
            
            // Add success message for the redirect
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "User " + userModel.getUsername() + " updated successfully");
        } catch (Exception e) {
            // Handle errors with appropriate message
            logger.error("Error updating user", e);
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Failed to update user: " + e.getMessage());
        }
        
        return DASHBOARD_REDIRECT;
    }
}