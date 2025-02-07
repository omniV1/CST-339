package com.gcu.agms.model.flight;

/**
 * Enum representing different types of aircraft that can be assigned to gates.
 * This classification is used for gate compatibility checking and resource allocation.
 */
public enum AircraftType {
    WIDE_BODY("Wide Body Aircraft"),
    NARROW_BODY("Narrow Body Aircraft"),
    REGIONAL_JET("Regional Jet");

    private final String description;

    AircraftType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}