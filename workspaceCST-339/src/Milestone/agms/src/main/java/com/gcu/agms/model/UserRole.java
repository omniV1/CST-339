package com.gcu.agms.model;

public enum UserRole {
    ADMIN("Administrator"),
    OPERATIONS_MANAGER("Operations Manager"),
    GATE_MANAGER("Gate Manager"),
    AIRLINE_STAFF("Airline Staff"),
    PUBLIC("Public User");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}