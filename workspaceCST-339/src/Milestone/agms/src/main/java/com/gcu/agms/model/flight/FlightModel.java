package com.gcu.agms.model.flight;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FlightModel {
    // Add ID field for database operations
    private Long id;
    
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
        SCHEDULED("Scheduled", FlightCssClasses.INFO, "Flight is scheduled"),
        BOARDING("Boarding", FlightCssClasses.PRIMARY, "Boarding in progress"),
        DEPARTED("Departed", FlightCssClasses.SUCCESS, "Flight has departed"),
        EN_ROUTE("En Route", FlightCssClasses.PRIMARY, "Flight is in the air"),
        APPROACHING("Approaching", FlightCssClasses.WARNING, "Approaching destination"),
        LANDED("Landed", FlightCssClasses.SUCCESS, "Flight has landed"),
        ARRIVED("Arrived", FlightCssClasses.SUCCESS, "Flight has arrived at gate"),
        DELAYED("Delayed", FlightCssClasses.WARNING, "Flight is delayed"),
        CANCELLED("Cancelled", FlightCssClasses.DANGER, "Flight is cancelled"),
        DIVERTED("Diverted", FlightCssClasses.DANGER, "Flight has been diverted"),
        COMPLETED("Completed", FlightCssClasses.SUCCESS, "Flight has completed");

        private final String label;
        private final String cssClass;
        private final String description;

        FlightStatus(String label, String cssClass, String description) {
            this.label = label;
            this.cssClass = cssClass;
            this.description = description;
        }

        public String getLabel() { return label; }
        public String getCssClass() { return cssClass; }
        public String getDescription() { return description; }
    }

    // Helper methods
    public boolean isActive() {
        return status == FlightStatus.BOARDING ||
               status == FlightStatus.DEPARTED ||
               status == FlightStatus.EN_ROUTE ||
               status == FlightStatus.APPROACHING;
    }

    public boolean isDelayed() {
        if (status == FlightStatus.DELAYED) return true;

        if (actualDeparture != null && scheduledDeparture != null) {
            return actualDeparture.isAfter(scheduledDeparture);
        }

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