package com.gcu.agms.controller.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * DashboardController handles routing to different role-specific dashboards.
 * 
 * This controller implements the Gateway pattern for the dashboard feature,
 * acting as a central entry point that routes users to their appropriate
 * dashboard based on their role. It's a key component in the application's
 * role-based access control (RBAC) system.
 * 
 * The controller maps the "/dashboard" endpoint, which is typically the default
 * success URL after login. It examines the authenticated user's role (obtained from
 * the Spring Security Authentication object) and redirects them to the appropriate
 * role-specific dashboard controller.
 * 
 * Role-based routing logic:
 * - ROLE_ADMIN → AdminDashboardController (/admin/dashboard)
 *   Full system access, user management, configuration
 * 
 * - ROLE_OPERATIONS_MANAGER → FlightOperationsController (/operations/dashboard)
 *   Flight scheduling, gate assignments, overall airport operations
 * 
 * - ROLE_GATE_MANAGER → GateDashboardController (/gates/dashboard)
 *   Gate management, maintenance scheduling, status updates
 * 
 * - ROLE_AIRLINE_STAFF → AirlineDashboardController (/airline/dashboard)
 *   Airline-specific flight information, requests
 * 
 * - ROLE_PUBLIC/Others → Home page (/)
 *   Public information only
 * 
 * This approach centralizes routing logic, simplifies security configuration,
 * and provides a single point of modification if role-based dashboard access
 * needs to change.
 */
@Controller
@Tag(name = "Dashboard Routing", description = "Central routing endpoint that directs users to their role-specific dashboards")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    
    /**
     * Logger for this class, used to record routing decisions and potential issues.
     * Logging is especially important in role-based routing to track access patterns
     * and troubleshoot authorization problems.
     */
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    /**
     * Handles routing to the appropriate dashboard based on the authenticated user's role.
     * 
     * This method:
     * 1. Verifies that the user is authenticated
     * 2. Extracts the user's role from their authorities
     * 3. Redirects to the appropriate dashboard controller based on role
     * 4. Logs the routing decision for auditing and troubleshooting
     * 
     * The routing logic uses the first authority found in the user's authentication.
     * In a system where users might have multiple roles, this would need enhancement
     * to determine the "highest" role for dashboard access.
     * 
     * @param authentication The Spring Security Authentication object containing
     *                      the user's identity and granted authorities (roles)
     * @return A redirect URL to the appropriate dashboard based on the user's role
     */
    @Operation(
        summary = "Route to role-specific dashboard",
        description = "Routes authenticated users to their appropriate dashboard based on their role. " +
                     "Admins go to admin dashboard, operations managers to operations dashboard, " +
                     "gate managers to gate dashboard, airline staff to airline dashboard, " +
                     "and public users to the home page."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "302", description = "Successfully redirected to role-specific dashboard"),
        @ApiResponse(responseCode = "401", description = "User is not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/dashboard")
    public String showDashboard(
            @Parameter(description = "Spring Security Authentication object containing user details and roles", required = true)
            Authentication authentication) { 
        
        // Security check: ensure user is authenticated before proceeding
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("User is not authenticated, redirecting to login.");
            return "redirect:/login";
        }

        // Log the routing attempt with username and authorities for audit trail
        logger.info("Routing dashboard for user: {}, Authorities: {}", 
            authentication.getName(), authentication.getAuthorities());

        // Extract the user's authority (role) from the authentication object
        // In this simple implementation, we take the first authority found
        // This could be enhanced to handle multiple roles with priority logic
        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst() // Simple approach: take the first role found
                .orElse("NONE"); 

        // Route to appropriate dashboard based on authority using switch expression
        // Each case includes logging for audit purposes and troubleshooting
        return switch(authority) {
            case "ROLE_ADMIN" -> {
                logger.info("Redirecting user {} to admin dashboard", authentication.getName());
                yield "redirect:/admin/dashboard";
            }
            case "ROLE_OPERATIONS_MANAGER" -> {
                logger.info("Redirecting user {} to operations dashboard", authentication.getName());
                yield "redirect:/operations/dashboard";
            }
            case "ROLE_GATE_MANAGER" -> {
                logger.info("Redirecting user {} to gate manager dashboard", authentication.getName());
                yield "redirect:/gates/dashboard";
            }
            case "ROLE_AIRLINE_STAFF" -> {
                 logger.info("Redirecting user {} to airline staff dashboard", authentication.getName());
                 yield "redirect:/airline/dashboard";
            }
             case "ROLE_PUBLIC" -> { 
                 // Public users are redirected to the general home page
                 // which contains only publicly accessible information
                 logger.info("Redirecting public user {} to home page", authentication.getName());
                 yield "redirect:/";
             }
            default -> {
                // Handle unexpected or custom roles with a default redirect
                // This provides graceful degradation if role configuration changes
                logger.warn("User {} has unrecognized authority '{}', redirecting to home page.", 
                    authentication.getName(), authority);
                yield "redirect:/"; // Default redirect for any other role or if no role found
            }
        };
    }
}