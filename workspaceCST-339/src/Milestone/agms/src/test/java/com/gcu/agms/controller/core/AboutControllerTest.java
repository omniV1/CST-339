// src/test/java/com/gcu/agms/controller/core/AboutControllerTest.java
package com.gcu.agms.controller.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@DisplayName("About Controller Tests")
class AboutControllerTest {

    private MockMvc mockMvc;
    private AboutController aboutController;

    @BeforeEach
    void setUp() {
        aboutController = new AboutController();
        mockMvc = MockMvcBuilders
            .standaloneSetup(aboutController)
            .setViewResolvers(viewResolver())
            .build();
    }

    private ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Test
    @DisplayName("Should return about view")
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
               .andExpect(status().isOk())
               .andExpect(view().name("about"));
    }

    @Test
    @DisplayName("Should return contact view") 
    void testContactPage() throws Exception {
        mockMvc.perform(get("/contact"))
               .andExpect(status().isOk())
               .andExpect(view().name("contact"));
    }
}