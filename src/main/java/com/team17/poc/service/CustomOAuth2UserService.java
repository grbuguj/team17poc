package com.team17.poc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 구글에서 가져온 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("구글 로그인 사용자 정보: {}", attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "name"  // 구글 OAuth2 기본 키 필드
        );
    }
}
