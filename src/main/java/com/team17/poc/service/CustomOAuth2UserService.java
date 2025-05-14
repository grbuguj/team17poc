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
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 카카오 로그인 추가로 인한 코드 추가 부분.
        // 어떤 로그인인지 구분: google, kakao 등
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 시도: {}", registrationId);

        // 구글에서 가져온 사용자 정보 -> 공통 사용자 정보로 수정.
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("가져온 사용자 정보: {}", attributes); // 카카오, 구글 두 개이므로, 하나로 합침.

        // 추가.
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        /* 새로운 추가 부분 */
        // 사용자 식별 정보 추출
        Map<String, Object> customAttributes = new HashMap<>();
        String name;

        if ("kakao".equals(registrationId)) {
            // 카카오 로그인일 경우
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            name = (String) profile.get("nickname");
            customAttributes.put("name", name);
            customAttributes.put("email", kakaoAccount.get("email"));
        } else {
            // 구글 로그인일 경우 (또는 기본)
            name = (String) attributes.get("name");
            customAttributes = attributes;
        }



        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "name"  // 구글 OAuth2 기본 키 필드
        );
    }
}
