package com.team17.poc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Team17 API 문서")
                        .version("v1.0")
                        .description("Team17 프로젝트의 백엔드 API 명세서입니다.")
                        .contact(new Contact()
                                .name("Team17")
                                .email("team17@example.com")));
    }
}
