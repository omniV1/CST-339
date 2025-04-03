package com.gcu.agms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Airport Gate Management System (AGMS)
 * 
 * This class serves as the entry point for the Spring Boot application and initializes
 * the application context. AGMS is designed to manage airport gate operations, 
 * including flight assignments, maintenance scheduling, and staff management.
 * 
 * Key features of the system include:
 * - User authentication and role-based access control
 * - Flight tracking and gate assignment
 * - Maintenance scheduling and tracking
 * - Administrative dashboard and reporting
 * 
 * The @SpringBootApplication annotation combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to automatically configure the application
 * - @ComponentScan: Tells Spring to scan for components in the current package
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.gcu.agms"})
public class AgmsApplication {
    
    /** 
     * Main method that serves as the entry point for the application.
     * 
     * This method delegates to Spring Boot's SpringApplication class to bootstrap
     * the application, creating the ApplicationContext and starting the embedded
     * web server. Upon startup, Spring Boot will:
     * 
     * 1. Set up default configuration
     * 2. Start the embedded server (Tomcat by default)
     * 3. Perform a component scan to detect controllers, services, and repositories
     * 4. Initialize the application context
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(AgmsApplication.class, args);
    }
}