package com.gcu.agms.model.auth;

/**
 * Enum defining the different user roles in the system.
 * Each role represents a specific access level and set of permissions.
 * The roles are hierarchical, with PUBLIC being the base role and
 * ADMIN having the highest level of access.
 */
public enum UserRole {
    PUBLIC("Public User"),
    AIRLINE_STAFF("Airline Staff"),
    GATE_MANAGER("Gate Manager"),
    OPERATIONS_MANAGER("Operations Manager"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}