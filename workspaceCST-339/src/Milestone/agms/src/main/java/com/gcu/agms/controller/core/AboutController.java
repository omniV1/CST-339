package com.gcu.agms.controller.core;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * AboutController is a Spring MVC controller that handles HTTP GET requests
 * for the "/about" and "/contact" URLs. It provides information about the
 * AGMS application and contact details.
 */
@Controller
@Tag(name = "Core", description = "Core application endpoints")
public class AboutController {
    
    /**
     * Provides information about the AGMS application
     */
    @GetMapping(value = "/about", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Get about page information",
        description = "Returns information about the AGMS application, its purpose, and features"
    )
    public Map<String, Object> getAboutInfo() {
        Map<String, Object> aboutInfo = new HashMap<>();
        aboutInfo.put("pageTitle", "About - AGMS");
        aboutInfo.put("applicationName", "Airport Gate Management System (AGMS)");
        aboutInfo.put("version", "1.0.0");
        aboutInfo.put("description", "A comprehensive system for managing airport gates, flights, and operations");
        aboutInfo.put("features", new String[]{
            "Flight Operations Management",
            "Gate Assignment Management",
            "Aircraft Maintenance Tracking",
            "Real-time Dashboard",
            "User Role Management"
        });
        return aboutInfo;
    }

    /**
     * Provides contact information
     */
    @GetMapping(value = "/contact", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(
        summary = "Get contact information",
        description = "Returns contact information for the AGMS support team"
    )
    public Map<String, String> getContactInfo() {
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("pageTitle", "Contact Us - AGMS");
        contactInfo.put("email", "support@agms.com");
        contactInfo.put("phone", "+1 (555) 123-4567");
        contactInfo.put("address", "123 Airport Way, Phoenix, AZ 85001");
        contactInfo.put("supportHours", "24/7");
        return contactInfo;
    }
}
