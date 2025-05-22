package com.team17.poc.config;

import com.team17.poc.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/oauth2/**", "/css/**",
                                "/js/**", "/images/**", "/favicon.ico", "/login",
                                "/authlogin", "/signup"
                        ).permitAll()
                        .requestMatchers(
                                "/barcode/**", "/ocr/**"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                // ✅ 세션 로그인: 로그인 form을 따로 사용하지 않기 때문에 비활성화 (API 기반)
                .formLogin(form -> form.disable())
                // ✅ 소셜 로그인: CustomOAuth2UserService 사용
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }

    // ✅ 비밀번호 암호화용 Bean 등록 (회원가입/로그인용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
