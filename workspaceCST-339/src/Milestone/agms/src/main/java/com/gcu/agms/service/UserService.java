package com.gcu.agms.service;

import java.util.ArrayList;
import java.util.List;  // Make sure to import the enum
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gcu.agms.model.UserModel;
import com.gcu.agms.model.UserRole;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
    private final List<UserModel> users = new ArrayList<>();

      // Add this method to help us debug
      public void printAllUsers() {
        System.out.println("=== Current Users in System ===");
        for (UserModel user : users) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Role: " + user.getRole());
            System.out.println("--------------------------");
        }
    }

    @PostConstruct
    public void initializeTestUsers() {
        // Create admin test user with proper enum role
        UserModel admin = new UserModel();
        admin.setUsername("admin");
        admin.setPassword("Admin123!");
        admin.setEmail("admin@agms.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("+1234567890");
        admin.setRole(UserRole.ADMIN);  // Using the enum
        users.add(admin);

        // Create operations manager test user
        UserModel ops = new UserModel();
        ops.setUsername("operations");
        ops.setPassword("Ops123!");
        ops.setEmail("ops@agms.com");
        ops.setFirstName("Operations");
        ops.setLastName("Manager");
        ops.setPhoneNumber("+1234567891");
        ops.setRole(UserRole.OPERATIONS_MANAGER);  // Using the enum
        users.add(ops);

        // Add other test users
        createTestUser("gate", "Gate123!", "gate@agms.com", 
                      "Gate", "Manager", "+1234567892", UserRole.GATE_MANAGER);
        createTestUser("airline", "Air123!", "airline@agms.com", 
                      "Airline", "Staff", "+1234567893", UserRole.AIRLINE_STAFF);
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
        user.setRole(role);  // Using the enum
        users.add(user);
    }

    public boolean registerUser(UserModel newUser) {
        System.out.println("Attempting to register new user:");
        System.out.println("Username: " + newUser.getUsername());
        System.out.println("Role: " + newUser.getRole());
        
        if (findByUsername(newUser.getUsername()).isPresent()) {
            System.out.println("Registration failed: Username already exists");
            return false;
        }
        
        users.add(newUser);
        System.out.println("Registration successful!");
        printAllUsers();
        return true;
    }

    public Optional<UserModel> findByUsername(String username) {
        return users.stream()
                   .filter(user -> user.getUsername().equals(username))
                   .findFirst();
    }

    public boolean authenticate(String username, String password) {
        System.out.println("Attempting to authenticate user:");
        System.out.println("Username: " + username);
        
        Optional<UserModel> user = findByUsername(username);
        if (user.isPresent()) {
            System.out.println("User found in system");
            boolean matches = user.get().getPassword().equals(password);
            System.out.println("Password match: " + matches);
            return matches;
        } else {
            System.out.println("User not found in system");
            return false;
        }
    }


    public List<UserModel> getAllUsers() {
        return new ArrayList<>(users);
    }
}