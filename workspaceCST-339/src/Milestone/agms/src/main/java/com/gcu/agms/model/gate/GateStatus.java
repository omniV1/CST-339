package com.gcu.agms.model.gate;

import com.gcu.agms.model.common.Status;

/**
 * Defines all possible operational states a gate can be in within the AGMS system.
 * 
 * This enum implements the common Status interface, providing a standardized way
 * to access status information across different domains in the application. It ensures
 * consistent status representation in both the business logic and user interface.
 * 
 * Gate statuses are critical for the gate assignment process as they determine 
 * whether a gate can be assigned to a flight. The status also affects how the
 * gate is displayed in the UI through the associated CSS classes.
 * 
 * Possible gate statuses include:
 * - AVAILABLE: Gate is free and can be assigned to flights
 * - OCCUPIED: Gate is currently in use by a flight
 * - MAINTENANCE: Gate is temporarily unavailable due to maintenance
 * - CLOSED: Gate is not operational (long-term unavailability)
 * - UNKNOWN: Default status when gate status cannot be determined
 */
public enum GateStatus implements Status {
    /**
     * Gate is free and can be assigned to flights.
     * Displayed with a success/green visual indicator in the UI.
     */
    AVAILABLE("Available", "success", "Gate is available for assignment"),
    
    /**
     * Gate is currently in use by a flight.
     * Displayed with a warning/yellow visual indicator in the UI.
     */
    OCCUPIED("Occupied", "warning", "Gate is currently in use"),
    
    /**
     * Gate is temporarily unavailable due to scheduled or emergency maintenance.
     * Displayed with a danger/red visual indicator in the UI.
     */
    MAINTENANCE("Maintenance", "danger", "Gate is under maintenance"),
    
    /**
     * Gate is not operational for an extended period.
     * This could be due to construction, upgrades, or decommissioning.
     * Displayed with a secondary/gray visual indicator in the UI.
     */
    CLOSED("Closed", "secondary", "Gate is not operational"),
    
    /**
     * Default status when the gate's current state cannot be determined.
     * This is typically used for newly created gates or when status data is missing.
     * Displayed with an info/blue visual indicator in the UI.
     */
    UNKNOWN("Unknown", "info", "Gate status cannot be determined");

    /**
     * User-friendly display label for the status
     */
    private final String label;
    
    /**
     * CSS class name for styling the status in the UI
     * Maps to Bootstrap color classes (success, warning, danger, etc.)
     */
    private final String cssClass;
    
    /**
     * Detailed description explaining the meaning of this status
     */
    private final String description;

    /**
     * Constructor for GateStatus enum values.
     * 
     * @param label Short display text shown in the UI
     * @param cssClass CSS class name for styling (typically Bootstrap color classes)
     * @param description Detailed explanation of the status
     */
    GateStatus(String label, String cssClass, String description) {
        this.label = label;
        this.cssClass = cssClass;
        this.description = description;
    }

    /**
     * Gets the user-friendly display label for this status.
     * 
     * @return The status label text
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * Gets the CSS class for styling this status in the UI.
     * These typically correspond to Bootstrap contextual classes
     * (success, warning, danger, info, etc.).
     * 
     * @return The CSS class name
     */
    @Override
    public String getCssClass() {
        return cssClass;
    }

    /**
     * Gets the detailed description of this status.
     * This provides a more complete explanation of what the status means.
     * 
     * @return The status description
     */
    @Override
    public String getDescription() {
        return description;
    }
}