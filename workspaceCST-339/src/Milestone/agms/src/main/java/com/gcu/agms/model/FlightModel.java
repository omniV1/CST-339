package com.gcu.agms.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a flight in the system with its schedule and operational details.
 * This model contains all essential information about a flight including its
 * identification, schedule, and current status.
 */
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
    
    @NotNull(message = "Aircraft type must be specified")
    private AircraftType aircraftType;
    
    @NotNull(message = "Scheduled departure time is required")
    private LocalDateTime scheduledDeparture;
    
    @NotNull(message = "Scheduled arrival time is required")
    private LocalDateTime scheduledArrival;
    
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    
    private FlightStatus status = FlightStatus.SCHEDULED;
    private String currentGate;
    
    /**
     * Checks if the flight is currently delayed
     * @return true if the flight is delayed, false otherwise
     */
    public boolean isDelayed() {
        if (status == FlightStatus.DELAYED) return true;
        if (actualDeparture != null && scheduledDeparture != null) {
            return actualDeparture.isAfter(scheduledDeparture);
        }
        return false;
    }
    
    /**
     * Updates the flight times and adjusts status accordingly
     * @param actual The actual time to update
     * @param isArrival true if updating arrival time, false for departure
     */
    public void updateTime(LocalDateTime actual, boolean isArrival) {
        if (isArrival) {
            this.actualArrival = actual;
            if (actual.isAfter(scheduledArrival)) {
                this.status = FlightStatus.DELAYED;
            }
        } else {
            this.actualDeparture = actual;
            if (actual.isAfter(scheduledDeparture)) {
                this.status = FlightStatus.DELAYED;
            }
        }
    }
}