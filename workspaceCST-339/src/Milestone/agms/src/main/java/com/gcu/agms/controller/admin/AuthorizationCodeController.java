package com.gcu.agms.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for managing authorization codes in the admin section.
 * Provides endpoints for creating, viewing, deactivating, and deleting authorization codes.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/auth-codes")
@Tag(name = "Authorization Code Management", description = "APIs for managing authorization codes")
public class AuthorizationCodeController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeController.class);
    
    private final AuthorizationCodeService authCodeService;
    
    /**
     * Constructor with service dependency injection.
     * 
     * @param authCodeService Service for authorization code management
     */
    public AuthorizationCodeController(AuthorizationCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }
    
    /**
     * Displays the list of authorization codes.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Get all authorization codes", description = "Retrieves a list of all authorization codes in the system")
    public List<AuthorizationCodeModel> getAuthCodes(HttpSession session) {
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code management");
            throw new SecurityException("Unauthorized access");
        }
        
        return authCodeService.getAllAuthCodes();
    }
    
    /**
     * Displays the form for creating a new authorization code.
     * 
     * @param model Model for the view
     * @param session HTTP session for user role verification
     * @return The view name
     */
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code creation");
            return "redirect:/login";
        }
        
        model.addAttribute("authCode", new AuthorizationCodeModel());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("pageTitle", "Create Authorization Code - AGMS");
        
        return "admin/auth-code-form";
    }
    
    /**
     * Creates a new authorization code.
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Create a new authorization code", description = "Generates a new authorization code with specified parameters")
    public AuthorizationCodeModel createAuthCode(
            @Parameter(description = "Authorization code details") @ModelAttribute AuthorizationCodeModel authCode,
            @Parameter(description = "Expiration date and time") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code creation");
            throw new SecurityException("Unauthorized access");
        }
        
        String code = authCodeService.generateNewCode(
            authCode.getRole(),
            authCode.getDescription(),
            expiresAt
        );
        
        authCode.setCode(code);
        return authCode;
    }
    
    /**
     * Deactivates an authorization code.
     */
    @PostMapping(value = "/{id}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Deactivate an authorization code", description = "Deactivates an existing authorization code")
    public boolean deactivateAuthCode(
            @Parameter(description = "ID of the authorization code") @PathVariable Long id,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code deactivation");
            throw new SecurityException("Unauthorized access");
        }
        
        return authCodeService.deactivateAuthCode(id);
    }
    
    /**
     * Deletes an authorization code.
     */
    @PostMapping(value = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Delete an authorization code", description = "Permanently removes an authorization code from the system")
    public void deleteAuthCode(
            @Parameter(description = "ID of the authorization code") @PathVariable Long id,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code deletion");
            throw new SecurityException("Unauthorized access");
        }
        
        authCodeService.deleteAuthCode(id);
    }
}