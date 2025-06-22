package com.team17.poc.config.handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFormLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 세션에 redirectUrl이 존재하면 해당 위치로 이동
        String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");

        if (redirectUrl != null) {
            request.getSession().removeAttribute("redirectUrl");
        } else {
            redirectUrl = "https://2025-unithon-team-17-fe.vercel.app"; // 기본값
        }

        response.sendRedirect(redirectUrl);
    }
}
