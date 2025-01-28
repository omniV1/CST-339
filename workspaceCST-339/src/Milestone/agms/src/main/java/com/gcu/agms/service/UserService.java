package com.gcu.agms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gcu.agms.model.UserModel;
import com.gcu.agms.model.UserRole;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    private final List<UserModel> users = new ArrayList<>();

    @PostConstruct
    public void init() {
        // Create test users for each role
        createTestUser("admin", "Admin123!", "admin@agms.com", UserRole.ADMIN);
        createTestUser("operations", "Ops123!", "ops@agms.com", UserRole.OPERATIONS_MANAGER);
        createTestUser("gate", "Gate123!", "gate@agms.com", UserRole.GATE_MANAGER);
        createTestUser("airline", "Air123!", "airline@agms.com", UserRole.AIRLINE_STAFF);
    }

    private void createTestUser(String username, String password, String email, UserRole role) {
        UserModel user = new UserModel();
        user.setFirstName("Test");
        user.setLastName(role.getDisplayName());
        user.setEmail(email);
        user.setPhoneNumber("+1234567890");
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        users.add(user);
    }

    public Optional<UserModel> findByUsername(String username) {
        return users.stream()
                   .filter(u -> u.getUsername().equals(username))
                   .findFirst();
    }

    public boolean authenticate(String username, String password) {
        return users.stream()
                   .anyMatch(u -> u.getUsername().equals(username) && 
                                 u.getPassword().equals(password));
    }

    public UserModel registerUser(UserModel user) {
        users.add(user);
        return user;
    }
}