package com.gcu.agms.controller.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@DisplayName("Home Controller Tests")
class HomeControllerTest {

    private MockMvc mockMvc;
    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
        
        // Configure view resolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Should display home page")
    void testDisplayHome() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(view().name("home"))
               .andExpect(model().attribute("pageTitle", "AGMS - Airport Gate Management System"))
               .andExpect(model().attributeExists("welcomeMessage"));
    }

    @Test 
    @DisplayName("Should handle index request")
    void testDisplayIndex() throws Exception {
        mockMvc.perform(get("/index"))
               .andExpect(status().isOk())
               .andExpect(view().name("home"))
               .andExpect(model().attribute("pageTitle", "AGMS - Airport Gate Management System"))
               .andExpect(model().attributeExists("welcomeMessage"));
    }
}

