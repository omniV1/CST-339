package com.gcu.agms.model.flight;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Flight Model class representing a flight in the Airport Gate Management System.
 * 
 * This class stores all information related to a flight including:
 * - Basic flight details (number, airline, origin, destination)
 * - Scheduling information (departure and arrival times)
 * - Gate assignments
 * - Current status and location
 * - Operational data (passenger count, remarks)
 * 
 * The class also includes validation annotations to ensure data integrity
 * when flights are created or updated through forms. It provides helper methods
 * to calculate derived flight information such as duration and delay status.
 * 
 * This model is central to the gate assignment functionality, as gates
 * are assigned to specific flights during specific time periods.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FlightModel {
    /**
     * Unique identifier for the flight record in the database
     */
    private Long id;
    
    /**
     * Flight number (typically numeric part without airline code)
     * Required field for flight identification
     */
    @NotEmpty(message = "Flight number is required")
    private String flightNumber;

    /**
     * IATA/ICAO airline code (e.g., UA for United Airlines)
     * Required field for airline identification
     */
    @NotEmpty(message = "Airline code is required")
    private String airlineCode;

    /**
     * Origin airport code (IATA/ICAO)
     * Required field indicating departure airport
     */
    @NotEmpty(message = "Origin is required")
    private String origin;

    /**
     * Destination airport code (IATA/ICAO)
     * Required field indicating arrival airport
     */
    @NotEmpty(message = "Destination is required")
    private String destination;

    /**
     * Scheduled departure time and date
     * Required field for flight scheduling
     */
    @NotNull(message = "Scheduled departure time is required")
    private LocalDateTime scheduledDeparture;

    /**
     * Scheduled arrival time and date
     * Required field for flight scheduling
     */
    @NotNull(message = "Scheduled arrival time is required")
    private LocalDateTime scheduledArrival;

    /**
     * Actual departure time, if different from scheduled
     * Null if flight hasn't departed or departed exactly on schedule
     */
    private LocalDateTime actualDeparture;
    
    /**
     * Actual arrival time, if different from scheduled
     * Null if flight hasn't arrived or arrived exactly on schedule
     */
    private LocalDateTime actualArrival;
    
    /**
     * Registration number of the aircraft assigned to this flight
     */
    private String assignedAircraft;    // Registration number of assigned aircraft
    
    /**
     * Current location or position of the flight
     * Used for tracking in-air flights
     */
    private String currentLocation;     // Current location/position
    
    /**
     * Current status of the flight (e.g., SCHEDULED, BOARDING, EN_ROUTE)
     * Defaults to SCHEDULED for new flights
     */
    private FlightStatus status = FlightStatus.SCHEDULED;

    /**
     * Operational flight details
     */
    private String departureGate;   // Gate assigned for departure
    private String arrivalGate;     // Gate assigned for arrival
    private String route;           // Flight route information
    private int passengerCount;     // Number of passengers on the flight
    private String remarks;         // Any special remarks or notes about the flight

    /**
     * Flight status enum representing possible states of a flight.
     * 
     * Each status includes:
     * - A display label for UI presentation
     * - A CSS class for styling in the UI
     * - A description explaining the status
     * 
     * This enum is used to track the flight's progress through its lifecycle,
     * from scheduling to completion, and to display appropriate visual indicators.
     */
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

        private final String label;        // User-friendly display name
        private final String cssClass;     // CSS class for styling in UI
        private final String description;  // Detailed explanation of status

        /**
         * Constructor for FlightStatus enum.
         * 
         * @param label User-friendly display name
         * @param cssClass CSS class for styling in UI
         * @param description Detailed explanation of status
         */
        FlightStatus(String label, String cssClass, String description) {
            this.label = label;
            this.cssClass = cssClass;
            this.description = description;
        }

        /**
         * Get the user-friendly display label for this status
         * @return The status label
         */
        public String getLabel() { return label; }
        
        /**
         * Get the CSS class for styling this status in the UI
         * @return The CSS class name
         */
        public String getCssClass() { return cssClass; }
        
        /**
         * Get the detailed description of this status
         * @return The status description
         */
        public String getDescription() { return description; }
    }

    /**
     * Determines if a flight is currently active (in progress).
     * 
     * A flight is considered active if it is:
     * - Boarding
     * - Has departed
     * - Is en route
     * - Is approaching its destination
     * 
     * This helps with filtering flights that require active monitoring.
     * 
     * @return true if the flight is currently active, false otherwise
     */
    public boolean isActive() {
        return status == FlightStatus.BOARDING ||
               status == FlightStatus.DEPARTED ||
               status == FlightStatus.EN_ROUTE ||
               status == FlightStatus.APPROACHING;
    }

    /**
     * Determines if a flight is delayed based on its status or actual times.
     * 
     * A flight is considered delayed if:
     * - Its status is explicitly set to DELAYED
     * - Its actual departure time is later than scheduled departure time
     * - Its actual arrival time is later than scheduled arrival time
     * 
     * @return true if the flight is delayed, false otherwise
     */
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

    /**
     * Gets the full flight identifier by combining airline code and flight number.
     * 
     * For example, if airlineCode is "UA" and flightNumber is "123",
     * this method returns "UA123".
     * 
     * @return The complete flight identifier (airline code + flight number)
     */
    public String getFlightIdentifier() {
        return airlineCode + flightNumber;
    }

    /**
     * Calculates the scheduled duration of the flight in minutes.
     * 
     * This is determined from the difference between scheduled arrival and
     * scheduled departure times.
     * 
     * @return The flight duration in minutes, or 0 if scheduling information is incomplete
     */
    public long getScheduledDuration() {
        if (scheduledDeparture != null && scheduledArrival != null) {
            return java.time.Duration.between(scheduledDeparture, scheduledArrival).toMinutes();
        }
        return 0;
    }
}