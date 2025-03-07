package com.gcu.topic04.business;

import org.springframework.stereotype.Service;

@Service
public class SecurityBusinessService {
    
    public boolean authenticate(String username, String password) {
        System.out.println("Hello from the SecurityBusinessService");
        // Simple authentication for demo purposes
        return true;
    }
}
