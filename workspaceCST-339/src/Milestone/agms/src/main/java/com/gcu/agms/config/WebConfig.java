package com.gcu.agms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // This tells Spring to look in the static folder for resources
        registry.addResourceHandler("/static/**")
               .addResourceLocations("classpath:/static/");
        
        // These are more specific mappings
        registry.addResourceHandler("/css/**")
               .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
               .addResourceLocations("classpath:/static/js/");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Add default view controllers if needed
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about").setViewName("about");
    }
}