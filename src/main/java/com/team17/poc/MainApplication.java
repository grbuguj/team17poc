package com.team17.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication  // Spring Boot의 메인 어노테이션
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);  // Spring Boot 애플리케이션 실행
    }
}

