package com.gcu.agms.config;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    @Test
    void testAddViewControllers() {
        ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
        ViewControllerRegistration registration = mock(ViewControllerRegistration.class);
        
        when(registry.addViewController("/")).thenReturn(registration);
        when(registry.addViewController("/about")).thenReturn(registration);
        
        webConfig.addViewControllers(registry);
        
        verify(registry).addViewController("/");
        verify(registry).addViewController("/about");
        verify(registration, times(2)).setViewName(anyString());
        verify(registration).setViewName("home");
        verify(registration).setViewName("about");
    }

    @Test
    void testAddResourceHandlers() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        
        when(registry.addResourceHandler(anyString())).thenReturn(registration);
        when(registration.addResourceLocations(anyString())).thenReturn(registration);
        
        webConfig.addResourceHandlers(registry);
        
        verify(registry).addResourceHandler("/static/**");
        verify(registry).addResourceHandler("/css/**");
        verify(registry).addResourceHandler("/js/**");
        
        verify(registration).addResourceLocations("classpath:/static/");
        verify(registration).addResourceLocations("classpath:/static/css/");
        verify(registration).addResourceLocations("classpath:/static/js/");
    }
}