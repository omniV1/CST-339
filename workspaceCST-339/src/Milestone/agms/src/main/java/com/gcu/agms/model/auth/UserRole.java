package com.gcu.agms.model.auth;

/**
 * Enum defining the different user roles in the Airport Gate Management System (AGMS).
 * 
 * This enum implements a role-based access control (RBAC) model where each role
 * represents a specific set of permissions and responsibilities within the system.
 * The roles are hierarchical, with PUBLIC being the most restricted role and
 * ADMIN having the highest level of access and permissions.
 * 
 * Role Hierarchy and Permissions:
 * 
 * 1. PUBLIC - Base level access
 *    - Can view public flight information
 *    - Can access general airport information
 *    - Limited to read-only access on public pages
 * 
 * 2. AIRLINE_STAFF - Airline employee access
 *    - All PUBLIC permissions
 *    - Can view assigned flight details
 *    - Can request gate changes
 *    - Can update flight status information
 * 
 * 3. GATE_MANAGER - Gate operation access
 *    - All AIRLINE_STAFF permissions
 *    - Can assign gates to flights
 *    - Can manage gate maintenance schedules
 *    - Can view all gates status
 * 
 * 4. OPERATIONS_MANAGER - Airport operations access
 *    - All GATE_MANAGER permissions
 *    - Can override gate assignments
 *    - Can manage staff assignments
 *    - Can generate operational reports
 *    - Can handle emergency procedures
 * 
 * 5. ADMIN - System administration access
 *    - All OPERATIONS_MANAGER permissions
 *    - Can manage user accounts
 *    - Can configure system settings
 *    - Full access to all system functionality
 *    - Can audit system activities
 */
public enum UserRole {
    /**
     * PUBLIC: Base role for general users
     * Provides access to public information only
     */
    PUBLIC("Public User"),
    
    /**
     * AIRLINE_STAFF: Role for airline employees
     * Provides access to flight management features
     */
    AIRLINE_STAFF("Airline Staff"),
    
    /**
     * GATE_MANAGER: Role for gate management personnel
     * Provides access to gate assignment features
     */
    GATE_MANAGER("Gate Manager"),
    
    /**
     * OPERATIONS_MANAGER: Role for airport operations staff
     * Provides access to overall operational management
     */
    OPERATIONS_MANAGER("Operations Manager"),
    
    /**
     * ADMIN: Highest role with full system access
     * Provides complete administrative control
     */
    ADMIN("Administrator");

    /**
     * Human-readable name of the role for display in UI
     */
    private final String displayName;

    /**
     * Constructor that sets the display name for each role
     * 
     * @param displayName The human-readable name of the role
     */
    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name of the role
     * Used in UI components like dropdowns and user profiles
     * 
     * @return String containing the display name of the role
     */
    public String getDisplayName() {
        return displayName;
    }
}