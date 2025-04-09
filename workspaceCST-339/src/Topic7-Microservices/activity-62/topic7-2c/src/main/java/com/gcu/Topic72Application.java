package com.gcu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Web Application
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Topic72Application {

    public static void main(String[] args) {
        SpringApplication.run(Topic72Application.class, args);
    }
} 