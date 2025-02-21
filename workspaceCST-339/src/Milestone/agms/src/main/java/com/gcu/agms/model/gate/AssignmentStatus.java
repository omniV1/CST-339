package com.gcu.agms.model.gate;

/**
 * Represents the possible states of a gate assignment.
 * This enum provides clear status tracking for assignments and includes
 * display information for the user interface.
 */
public enum AssignmentStatus {
    SCHEDULED("Scheduled", "primary"),
    IN_PROGRESS("In Progress", "warning"),
    ACTIVE("Active", "success"),
    COMPLETED("Completed", "info"),
    CANCELLED("Cancelled", "danger"),
    DELAYED("Delayed", "warning");

    private final String label;
    private final String cssClass;

    AssignmentStatus(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}