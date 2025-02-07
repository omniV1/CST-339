package com.gcu.agms.model.gate;

/**
 * Defines all possible states a gate can be in within the AGMS system.
 * According to the UML class diagram, this enum maintains a many-to-one relationship
 * with the Gate class, where each gate must have exactly one status at any time.
 * 
 * The statuses represent different operational states of a gate:
 * - AVAILABLE: Ready for assignment
 * - OCCUPIED: Currently in use by a flight
 * - MAINTENANCE: Under maintenance and unavailable
 * - CLOSED: Not operational
 */
public enum GateStatus {
    AVAILABLE("Available", "success", "Gate is available for assignment"),
    OCCUPIED("Occupied", "warning", "Gate is currently in use"),
    MAINTENANCE("Maintenance", "danger", "Gate is under maintenance"),
    CLOSED("Closed", "secondary", "Gate is not operational"),
    UNKNOWN("Unknown", "info", "Gate status cannot be determined");

    private final String label;
    private final String cssClass;
    private final String description;

    GateStatus(String label, String cssClass, String description) {
        this.label = label;
        this.cssClass = cssClass;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getDescription() {
        return description;
    }
}
