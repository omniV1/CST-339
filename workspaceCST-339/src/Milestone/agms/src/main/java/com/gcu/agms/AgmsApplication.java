package com.gcu.agms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Airport Gate Management System (AGMS)
 * This class serves as the entry point for the Spring Boot application
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.gcu.agms"})
public class AgmsApplication {
    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AgmsApplication.class, args);
    }
}