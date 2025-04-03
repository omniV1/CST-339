package com.gcu.agms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.gcu.agms.service.AppUserDetailsService;

/**
 * Spring Security Configuration for the AGMS application.
 * 
 * This class configures all security aspects of the application including:
 * - Authentication mechanism (form-based login)
 * - Authorization rules (URL-based access control)
 * - Password encoding
 * - Login/logout handling
 * - Session management
 * 
 * The configuration uses Spring Security's modern approach with the SecurityFilterChain bean
 * instead of the deprecated WebSecurityConfigurerAdapter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Custom UserDetailsService implementation that connects Spring Security
     * with our application's user database and authentication logic.
     */
    @Autowired
    AppUserDetailsService userDetailsService; // Autowire the service we created

    /**
     * Configures the security filter chain that processes all HTTP requests.
     * 
     * This method defines:
     * 1. URL-based access control rules
     * 2. Custom login form configuration
     * 3. Logout handling
     * 4. Session management
     * 
     * @param http The HttpSecurity object to configure
     * @return The configured SecurityFilterChain
     * @throws Exception If configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configure URL-based authorization rules
            .authorizeHttpRequests(authorize -> authorize
                // Public resources accessible without authentication
                .requestMatchers("/", "/login", "/register", "/images/**", "/css/**", "/js/**").permitAll()
                
                // Role-specific access restrictions
                .requestMatchers("/admin/**").hasRole("ADMIN")               // Admin-only pages
                .requestMatchers("/operations/**").hasRole("OPERATIONS_MANAGER") // Operations manager pages
                .requestMatchers("/gates/**").hasRole("GATE_MANAGER")        // Gate manager pages
                .requestMatchers("/airline/**").hasRole("AIRLINE_STAFF")     // Airline staff pages
                
                // All other URLs require authentication (user must be logged in)
                .anyRequest().authenticated()
            )
            
            // Configure custom login form
            .formLogin(form -> form
                .loginPage("/login")                      // Custom login page URL
                .loginProcessingUrl("/perform_login")     // Form submission endpoint
                .defaultSuccessUrl("/dashboard", true)    // Redirect after successful login
                .failureUrl("/login?error=true")          // Redirect after failed login
                .permitAll()                              // Allow access to login page for everyone
            )
            
            // Configure logout behavior
            .logout(logout -> logout
                .logoutUrl("/logout")                     // Logout endpoint URL
                .logoutSuccessUrl("/login?logout=true")   // Redirect after successful logout
                .invalidateHttpSession(true)              // Invalidate HTTP session
                .deleteCookies("JSESSIONID")              // Delete session cookies
                .permitAll()                              // Allow logout for everyone
            )
            
            // Use our custom user details service for authentication
            .userDetailsService(userDetailsService);

        return http.build();
    }

    /**
     * Creates a PasswordEncoder bean for secure password handling.
     * 
     * BCrypt is a strong adaptive hashing function designed specifically for passwords.
     * It automatically handles salt generation and includes the salt in the hash output.
     * The default strength parameter (10) provides a good balance between security and performance.
     * 
     * @return A BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for strong password hashing
        return new BCryptPasswordEncoder(); 
    }
} 