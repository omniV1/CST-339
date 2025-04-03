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
    
    /**
     * Displays the main administrative dashboard with comprehensive system overview.
     * 
     * This method aggregates data from multiple services to provide administrators
     * with a holistic view of the system status including:
     * - User statistics and management access
     * - Gate status distribution across all terminals
     * - Operational statistics for monitoring
     * - Terminal-specific gate information
     * 
     * The dashboard serves as the central hub for administrators to monitor
     * system health and access various management functions.
     * 
     * @param model Spring MVC Model for passing data to the view
     * @param session HTTP session for user context
     * @return View name for the admin dashboard template
     */
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
    
    /**
     * Displays the system-wide gate management interface.
     * 
     * This page provides administrators with a comprehensive view of all gates
     * in the system and tools to manage them. It shows:
     * - All gates across all terminals
     * - Current status of each gate
     * - Options to create, edit, or manage gates
     * 
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the gate management template
     */
    @GetMapping("/gates")
    public String showGateManagement(Model model) {
        logger.info("Loading gate management view");
        
        // Set page title and add all gates to the model
        model.addAttribute(PAGE_TITLE_ATTR, "Gate Management - AGMS");
        model.addAttribute(GATES_ATTR, gateManagementService.getAllGates());
        
        return GATES_VIEW;
    }
    
    /**
     * Displays the form for creating a new gate in the system.
     * 
     * This endpoint presents a form with all fields required to define
     * a new gate including:
     * - Gate ID and terminal information
     * - Gate type and size configurations
     * - Equipment and feature settings
     * 
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the gate creation form template
     */
    @GetMapping("/gates/create")
    public String showCreateGateForm(Model model) {
        logger.info("Displaying gate creation form");
        
        // Set page title and add empty gate model for form binding
        model.addAttribute(PAGE_TITLE_ATTR, "Create New Gate - AGMS");
        model.addAttribute(GATE_MODEL_ATTR, new GateModel());
        
        return GATE_FORM_VIEW;
    }
    
    /**
     * Processes the gate creation form submission.
     * 
     * This method:
     * 1. Validates the gate data using JSR-303 annotations
     * 2. Attempts to create the gate through the GateManagementService
     * 3. Handles success or failure with appropriate feedback messages
     * 4. Redirects to prevent form resubmission on refresh
     * 
     * @param gateModel The gate data submitted from the form
     * @param result Validation results for the gate model
     * @param redirectAttributes Used to pass flash attributes through redirect
     * @return Redirect URL or form view depending on the result
     */
    @PostMapping("/gates/create")
    public String createGate(@Valid GateModel gateModel, 
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
    
    /**
     * Displays the form for adding a new user to the system.
     * 
     * This endpoint presents a form with all fields required to create
     * a new user account including:
     * - User credentials (username, password)
     * - Personal information (name, email, phone)
     * - Role and permissions settings
     * 
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the user creation form template
     */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        // Set page title and add empty user model for form binding
        model.addAttribute(USER_MODEL_ATTR, new UserModel());
        model.addAttribute(PAGE_TITLE_ATTR, "Add New User - AGMS");
        
        return USER_FORM_VIEW;
    }

    /**
     * Processes the user creation form submission.
     * 
     * This method:
     * 1. Validates the user data using JSR-303 annotations
     * 2. Attempts to register the user through the UserService
     * 3. Handles success or failure with appropriate feedback messages
     * 4. Redirects to prevent form resubmission on refresh
     * 
     * @param userModel The user data submitted from the form
     * @param result Validation results for the user model
     * @param redirectAttributes Used to pass flash attributes through redirect
     * @return Redirect URL or form view depending on the result
     */
    @PostMapping("/users/add")
    public String addUser(@Valid UserModel userModel, 
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
    
    /**
     * Displays the user management interface with a list of all users.
     * 
     * This page provides administrators with a comprehensive view of all users
     * in the system and tools to manage them. It shows:
     * - All users with their roles and status
     * - Options to edit or delete users
     * - Access to add new users
     * 
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the user management template
     */
    @GetMapping("/users")
    public String showUsers(Model model) {
        // Set page title and add all users to the model
        model.addAttribute(USERS_ATTR, userService.getAllUsers());
        model.addAttribute(PAGE_TITLE_ATTR, "User Management - AGMS");
        
        return USERS_VIEW;
    }
    
    /**
     * Displays system health and monitoring information.
     * 
     * This page provides comprehensive system status information including:
     * - System performance metrics
     * - Database connection status
     * - Gate operational statistics
     * - Application health indicators
     * 
     * It serves as a central monitoring dashboard for system administrators.
     * 
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the system health template
     */
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

    /**
     * Handles the deletion of a user from the system.
     * 
     * This method:
     * 1. Attempts to delete the user with the specified ID
     * 2. Logs the deletion attempt for audit purposes
     * 3. Provides feedback on success or failure
     * 4. Handles exceptions that may occur during deletion
     * 
     * @param id The database ID of the user to delete
     * @param redirectAttributes Used to pass flash attributes through redirect
     * @return Redirect URL to the dashboard
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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

    /**
     * Displays the form for editing an existing user.
     * 
     * This method:
     * 1. Retrieves the user with the specified ID
     * 2. Populates the form with the user's current data
     * 3. Allows modifications to user information
     * 
     * @param id The database ID of the user to edit
     * @param model Spring MVC Model for passing data to the view
     * @return View name for the user edit form or redirect if user not found
     */
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
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

    /**
     * Processes the user update form submission.
     * 
     * This method:
     * 1. Validates the updated user data
     * 2. Attempts to update the user through the UserService
     * 3. Handles validation errors by returning to the form
     * 4. Provides feedback on success or failure
     * 5. Handles exceptions during the update process
     * 
     * @param id The database ID of the user to update
     * @param userModel The updated user data submitted from the form
     * @param result Validation results for the user model
     * @param redirectAttributes Used to pass flash attributes through redirect
     * @return Redirect URL or form view depending on the result
     */
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, 
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