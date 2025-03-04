package com.gcu.agms.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.auth.AuthorizationCodeModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;

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
     * 
     * @param model Model for the view
     * @param session HTTP session for user role verification
     * @return The view name
     */
    @GetMapping
    public String showAuthCodes(Model model, HttpSession session) {
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code management");
            return "redirect:/login";
        }
        
        List<AuthorizationCodeModel> authCodes = authCodeService.getAllAuthCodes();
        model.addAttribute("authCodes", authCodes);
        model.addAttribute("pageTitle", "Authorization Codes - AGMS");
        
        return "admin/auth-codes";
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
     * Processes the creation of a new authorization code.
     * 
     * @param authCode The authorization code model from the form
     * @param expiresAt Optional expiration date
     * @param redirectAttributes Redirect attributes for flash messages
     * @param session HTTP session for user role verification
     * @return Redirect URL
     */
    @PostMapping("/create")
    public String createAuthCode(
            @ModelAttribute AuthorizationCodeModel authCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code creation");
            return "redirect:/login";
        }
        
        try {
            String code = authCodeService.generateNewCode(
                authCode.getRole(),
                authCode.getDescription(),
                expiresAt
            );
            
            redirectAttributes.addFlashAttribute("success",
                "Authorization code created successfully: " + code);
        } catch (Exception e) {
            logger.error("Error creating authorization code", e);
            redirectAttributes.addFlashAttribute("error",
                "Failed to create authorization code: " + e.getMessage());
        }
        
        return "redirect:/admin/auth-codes";
    }
    
    /**
     * Deactivates an authorization code.
     * 
     * @param id The ID of the code to deactivate
     * @param redirectAttributes Redirect attributes for flash messages
     * @param session HTTP session for user role verification
     * @return Redirect URL
     */
    @PostMapping("/{id}/deactivate")
    public String deactivateAuthCode(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code deactivation");
            return "redirect:/login";
        }
        
        try {
            boolean deactivated = authCodeService.deactivateAuthCode(id);
            
            if (deactivated) {
                redirectAttributes.addFlashAttribute("success",
                    "Authorization code deactivated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error",
                    "Authorization code not found");
            }
        } catch (Exception e) {
            logger.error("Error deactivating authorization code", e);
            redirectAttributes.addFlashAttribute("error",
                "Failed to deactivate authorization code: " + e.getMessage());
        }
        
        return "redirect:/admin/auth-codes";
    }
    
    /**
     * Deletes an authorization code.
     * 
     * @param id The ID of the code to delete
     * @param redirectAttributes Redirect attributes for flash messages
     * @param session HTTP session for user role verification
     * @return Redirect URL
     */
    @PostMapping("/{id}/delete")
    public String deleteAuthCode(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        // Verify admin role
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            logger.warn("Unauthorized access attempt to auth code deletion");
            return "redirect:/login";
        }
        
        try {
            authCodeService.deleteAuthCode(id);
            redirectAttributes.addFlashAttribute("success",
                "Authorization code deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting authorization code", e);
            redirectAttributes.addFlashAttribute("error",
                "Failed to delete authorization code: " + e.getMessage());
        }
        
        return "redirect:/admin/auth-codes";
    }
}