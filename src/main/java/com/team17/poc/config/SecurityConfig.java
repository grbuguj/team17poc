package com.team17.poc.config;

import com.team17.poc.auth.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://localhost:5173",
                            "http://keepbara.duckdns.org",
                            "http://keepbara.duckdns.org:8082",
                            "http://localhost:8082",
                            "https://keepbara.duckdns.org",
                            "https://2025-unithon-team-17-fe.vercel.app"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/oauth2/**", "/css/**",
                                "/js/**", "/images/**", "/favicon.ico", "/login",
                                "/authlogin", "/signup", "/swagger-ui.html",
                                "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**",
                                "/api/box/items/shot-barcode",
                                "/api/box/items/shot-expire",
                                "/api/box/items/session-id",
                                "/api/box/locations/**", "/api/box/**",
                                "/product-test.html", "/uploads/**"
                        ).permitAll()

                        // ✅ 상품 목록/상세 조회는 모두 허용
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ✅ 상품 등록/수정/삭제는 로그인 필요
                        .requestMatchers(HttpMethod.POST, "/api/products").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        // ✅ 기타는 인증 필요
                        .requestMatchers("/barcode/**", "/ocr/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다")
                        )
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("https://2025-unithon-team-17-fe.vercel.app", true)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("https://2025-unithon-team-17-fe.vercel.app", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
