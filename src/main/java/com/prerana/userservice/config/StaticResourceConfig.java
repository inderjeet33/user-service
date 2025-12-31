package com.prerana.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    // Inject the upload directory path from application.properties
    // Ensure you use file separators appropriate for your OS (double backslashes for Windows)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map requests starting with "/uploads/**" to the physical directory location
        registry.addResourceHandler("/uploads/**")
                // On Windows, the file protocol requires three slashes: file:///
                // and the path should end with a slash for a directory
                .addResourceLocations("file:///" + uploadDir + "/");
    }
}
