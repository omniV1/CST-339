package com.gcu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Users REST API Service
 * Note: At runtime, service discovery will be active from spring-cloud-starter-netflix-eureka-client
 */
@SpringBootApplication(scanBasePackages = {"com.gcu"})
@EnableEurekaClient
public class Topic72Application {

    public static void main(String[] args) {
        SpringApplication.run(Topic72Application.class, args);
    }
} 