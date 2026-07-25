package com.hotel.management.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use File.toURI() to handle Windows backslashes correctly
        // Produces: file:///C:/Users/.../uploads/
        String uploadPath = new File(System.getProperty("user.dir"), "uploads")
                .toURI()
                .toString();


        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow the frontend to load uploaded images directly from the backend
        registry.addMapping("/uploads/**")
                .allowedOrigins(Arrays.asList(allowedOrigins).toArray(String[]::new))
                .allowedMethods("GET");
    }
}