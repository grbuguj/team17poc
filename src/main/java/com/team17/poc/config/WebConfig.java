package com.team17.poc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${product.upload-dir}")
    private String uploadDir;

    @Value("${file.upload-dir}")
    private String generalUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + generalUploadDir + "/");
    }


}
