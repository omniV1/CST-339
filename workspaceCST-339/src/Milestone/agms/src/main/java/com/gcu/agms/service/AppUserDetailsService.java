package com.gcu.agms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.repository.UserRepository;

/**
 * Implementation of Spring Security's UserDetailsService interface.
 * 
 * This service acts as the bridge between our application's user database
 * and Spring Security's authentication system. It is responsible for:
 * 
 * 1. Loading user data from our custom UserRepository when requested by Spring Security
 * 2. Converting our application's UserModel into Spring Security's UserDetails
 * 3. Setting up proper authorization by mapping our roles to Spring Security authorities
 * 4. Verifying user existence and providing appropriate error handling
 * 
 * When a user attempts to log in, Spring Security calls the loadUserByUsername method
 * to retrieve the user details needed for authentication. The returned UserDetails object
 * contains the credentials and authorities that Spring Security uses to:
 * - Validate the provided password against the stored password
 * - Determine what resources the user is authorized to access
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    /**
     * Repository for user data access
     * Automatically injected by Spring's dependency injection
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their username from the application's user database.
     * 
     * This is the core method required by Spring Security's authentication process.
     * It converts our custom UserModel into Spring Security's UserDetails format.
     * 
     * The method:
     * 1. Retrieves the user from our repository by username
     * 2. Throws an exception if the user is not found
     * 3. Creates appropriate authorities based on the user's role
     * 4. Returns a UserDetails object that Spring Security can use for authentication
     * 
     * @param username The username identifying the user to load
     * @return A UserDetails object containing the user's credentials and authorities
     * @throws UsernameNotFoundException If the user cannot be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user in our database or throw an exception if not found
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        // Create a list to hold the user's authorities (roles)
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Add the user's role as a Spring Security authority
        // Spring Security expects roles to be prefixed with "ROLE_"
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userModel.getRole().name())); 

        // Adapt our UserModel to Spring Security's UserDetails interface
        // This provides Spring Security with everything it needs for authentication and authorization
        return new User(
                userModel.getUsername(),           // Username for authentication
                userModel.getPassword(),           // Encoded password for verification
                userModel.isEnabled(),             // Whether the user account is enabled
                true,                              // Account is not expired
                true,                              // Credentials are not expired
                true,                              // Account is not locked
                authorities);                      // The user's authorities/roles
    }
} 