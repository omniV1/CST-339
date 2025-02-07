package com.gcu.agms.model.flight;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightModel {
    @NotEmpty(message = "Flight number is required")
    private String flightNumber;

    @NotEmpty(message = "Airline code is required")
    private String airlineCode;

    @NotEmpty(message = "Origin is required")
    private String origin;

    @NotEmpty(message = "Destination is required")
    private String destination;

    @NotNull(message = "Scheduled departure time is required")
    private LocalDateTime scheduledDeparture;

    @NotNull(message = "Scheduled arrival time is required")
    private LocalDateTime scheduledArrival;

    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    private String assignedAircraft;    // Registration number of assigned aircraft
    private String currentLocation;     // Current location/position
    private FlightStatus status = FlightStatus.SCHEDULED;

    // Route and operational details
    private String departureGate;
    private String arrivalGate;
    private String route;
    private int passengerCount;
    private String remarks;

    public enum FlightStatus {
        // Update enum constants to include all three parameters:
        // Format: NAME(display label, CSS class, description)
        SCHEDULED("Scheduled", "info", "Flight is scheduled"),
        BOARDING("Boarding", "primary", "Boarding in progress"),
        DEPARTED("Departed", "success", "Flight has departed"),
        EN_ROUTE("En Route", "primary", "Flight is in the air"),
        APPROACHING("Approaching", "warning", "Approaching destination"),
        LANDED("Landed", "success", "Flight has landed"),
        ARRIVED("Arrived", "success", "Flight has arrived at gate"),
        DELAYED("Delayed", "warning", "Flight is delayed"),
        CANCELLED("Cancelled", "danger", "Flight is cancelled"),
        DIVERTED("Diverted", "danger", "Flight has been diverted");

        private final String label;
        private final String cssClass;
        private final String description;

        FlightStatus(String label, String cssClass, String description) {
            this.label = label;
            this.cssClass = cssClass;
            this.description = description;
        }

        // Ensure all getter methods are properly defined
        public String getLabel() { return label; }
        public String getCssClass() { return cssClass; }
        public String getDescription() { return description; }
    }


    // Helper method to check if flight is active (meaning it's in progress)
    public boolean isActive() {
        return status == FlightStatus.BOARDING ||
               status == FlightStatus.DEPARTED ||
               status == FlightStatus.EN_ROUTE ||
               status == FlightStatus.APPROACHING;
    }

    // Helper method to check if flight is delayed
    public boolean isDelayed() {
        if (status == FlightStatus.DELAYED) return true;

        // Check if actual departure is later than scheduled
        if (actualDeparture != null && scheduledDeparture != null) {
            return actualDeparture.isAfter(scheduledDeparture);
        }

        // Check if actual arrival is later than scheduled
        if (actualArrival != null && scheduledArrival != null) {
            return actualArrival.isAfter(scheduledArrival);
        }

        return false;
    }

    // Helper method to get full flight identifier
    public String getFlightIdentifier() {
        return airlineCode + flightNumber;
    }

    // Helper method to calculate flight duration in minutes
    public long getScheduledDuration() {
        if (scheduledDeparture != null && scheduledArrival != null) {
            return java.time.Duration.between(scheduledDeparture, scheduledArrival).toMinutes();
        }
        return 0;
    }
}