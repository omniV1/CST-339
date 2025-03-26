package com.gcu.agms.model.gate;

import com.gcu.agms.model.common.Status;

/**
 * Defines all possible states a gate can be in within the AGMS system.
 * This enum now implements the common Status interface, which standardizes
 * the way status information is accessed across different domains.
 */
public enum GateStatus implements Status {
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

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String getDescription() {
        return description;
    }
}