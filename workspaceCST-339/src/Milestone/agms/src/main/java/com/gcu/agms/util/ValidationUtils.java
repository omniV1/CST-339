package com.gcu.agms.util;

import java.util.regex.Pattern;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.gate.GateModel;

/**
 * Utility class for common validation logic.
 * This class provides static methods for validating different types of entities,
 * extracting common validation patterns from service implementations.
 */
public final class ValidationUtils {
    
    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w+.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{7,14}$");
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    /**
     * Validates a user model for registration.
     * 
     * @param user The user model to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateUser(UserModel user) {
        if (user == null || 
            isNullOrEmpty(user.getUsername()) ||
            isNullOrEmpty(user.getEmail()) || 
            isNullOrEmpty(user.getFirstName()) ||
            isNullOrEmpty(user.getLastName())) {
            return false;
        }

        // Email validation
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return false;
        }

        // For registrations, password is required
        if (user.getId() == null) {
            if (isNullOrEmpty(user.getPassword()) ||
                isNullOrEmpty(user.getPhoneNumber())) {
                return false;
            }

            // Phone validation
            if (!PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
                return false;
            }

            // Password validation
            return validatePassword(user.getPassword());
        }

        // For updates, if phone number is provided, validate it
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
                return false;
            }
        }

        // For updates, if password is provided, validate it
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            return validatePassword(user.getPassword());
        }

        return true;
    }
    
    /**
     * Validates a password for strength requirements.
     * 
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Use possessive quantifiers, anchors, and concise character classes
        boolean hasUpper = password.matches("^[^A-Z]*+[A-Z][\\s\\S]*+$");
        boolean hasLower = password.matches("^[^a-z]*+[a-z][\\s\\S]*+$");
        boolean hasDigit = password.matches("^\\D*+\\d[\\s\\S]*+$");
        boolean hasSpecial = password.matches("^[^@#$%^&+=!]*+[@#$%^&+=!][\\s\\S]*+$");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Validates a flight model for creation and updates.
     * 
     * @param flight The flight model to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateFlight(FlightModel flight) {
        return flight != null &&
               !isNullOrEmpty(flight.getFlightNumber()) &&
               !isNullOrEmpty(flight.getAirlineCode()) &&
               !isNullOrEmpty(flight.getOrigin()) &&
               !isNullOrEmpty(flight.getDestination()) &&
               flight.getScheduledDeparture() != null &&
               flight.getScheduledArrival() != null &&
               flight.getStatus() != null &&
               flight.getScheduledArrival().isAfter(flight.getScheduledDeparture());
    }
    
    /**
     * Validates a gate model for creation and updates.
     * 
     * @param gate The gate model to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateGate(GateModel gate) {
        return gate != null && 
            !isNullOrEmpty(gate.getGateId()) &&
            !isNullOrEmpty(gate.getTerminal()) &&
            !isNullOrEmpty(gate.getGateNumber()) &&
            gate.getGateType() != null &&
            gate.getGateSize() != null &&
            gate.getStatus() != null;
    }
    
    /**
     * Checks if a string is null or empty.
     * 
     * @param str The string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}