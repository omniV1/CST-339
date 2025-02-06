package com.gcu.agms.model;

/**
 * Enum representing the different possible states of a flight.
 * Each status includes a display label and CSS class for UI representation.
 */
public enum FlightStatus {
    SCHEDULED("Scheduled", "primary"),
    BOARDING("Boarding", "info"),
    DEPARTED("Departed", "success"),
    ARRIVED("Arrived", "success"),
    DELAYED("Delayed", "warning"),
    CANCELLED("Cancelled", "danger");

    private final String label;
    private final String cssClass;

    FlightStatus(String label, String cssClass) {
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