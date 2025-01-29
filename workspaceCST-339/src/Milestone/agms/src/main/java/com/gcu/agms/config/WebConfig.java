package com.gcu.agms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig class implements WebMvcConfigurer to configure resource handlers and view controllers.
 * 
 * <p>This configuration class is annotated with @Configuration to indicate that it is a source of 
 * bean definitions. It overrides methods from WebMvcConfigurer to customize the default Spring MVC 
 * configuration.</p>
 * 
 * <p>The addResourceHandlers method is used to specify the locations of static resources such as 
 * CSS and JavaScript files. It maps URL patterns to the corresponding resource locations in the 
 * classpath.</p>
 * 
 * <p>The addViewControllers method is used to configure simple automated controllers pre-configured 
 * with a response status code and/or a view to render the response body. It maps URL paths to view 
 * names.</p>
 * 
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 * @see org.springframework.context.annotation.Configuration
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    
    /** 
     * @param registry
     */
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

    
    /** 
     * @param registry
     */
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Add default view controllers if needed
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/about").setViewName("about");
    }
}