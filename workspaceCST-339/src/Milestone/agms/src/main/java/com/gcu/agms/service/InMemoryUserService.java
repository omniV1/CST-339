package com.gcu.agms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.UserModel;
import com.gcu.agms.model.UserRole;

import jakarta.annotation.PostConstruct;

/**
 * Provides an in-memory implementation of the UserService interface.
 * This implementation stores user data in memory and is suitable for
 * development and testing purposes. In a production environment, this
 * would typically be replaced with a database-backed implementation.
 */
@Service
public class InMemoryUserService implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserService.class);
    private final List<UserModel> users = new ArrayList<>();

    @PostConstruct
    public void initializeTestUsers() {
        logger.info("Initializing in-memory test users");
        createTestUser("admin", "Admin123!", "admin@agms.com", 
                      "Admin", "User", "+1234567890", UserRole.ADMIN);
        createTestUser("operations", "Ops123!", "ops@agms.com",
                      "Operations", "Manager", "+1234567891", UserRole.OPERATIONS_MANAGER);
        createTestUser("gate", "Gate123!", "gate@agms.com",
                      "Gate", "Manager", "+1234567892", UserRole.GATE_MANAGER);
        createTestUser("airline", "Air123!", "airline@agms.com",
                      "Airline", "Staff", "+1234567893", UserRole.AIRLINE_STAFF);
        logger.info("Test users initialized successfully");
    }

    private void createTestUser(String username, String password, String email,
                              String firstName, String lastName, String phone,
                              UserRole role) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phone);
        user.setRole(role);
        users.add(user);
        logger.debug("Created test user: {}", username);
    }

    @Override
    public boolean registerUser(UserModel newUser) {
        logger.info("Attempting to register new user: {}", newUser.getUsername());
        if (findByUsername(newUser.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username already exists");
            return false;
        }
        users.add(newUser);
        logger.info("User registered successfully: {}", newUser.getUsername());
        return true;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        logger.debug("Searching for user: {}", username);
        return users.stream()
                   .filter(user -> user.getUsername().equals(username))
                   .findFirst();
    }

    @Override
    public boolean authenticate(String username, String password) {
        logger.info("Attempting to authenticate user: {}", username);
        Optional<UserModel> user = findByUsername(username);
        boolean authenticated = user.map(u -> u.getPassword().equals(password))
                                  .orElse(false);
        logger.info("Authentication {}: {}", authenticated ? "successful" : "failed", username);
        return authenticated;
    }

    @Override
    public List<UserModel> getAllUsers() {
        logger.debug("Retrieving all users");
        return new ArrayList<>(users);
    }
}