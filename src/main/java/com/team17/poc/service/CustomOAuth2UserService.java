package com.team17.poc.service;

import com.team17.poc.entity.Member;
import com.team17.poc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId(); // "google" or "kakao"
        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // ex: "sub", "id"

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> extractedAttributes = new HashMap<>();
        String providerId;
        String name;
        String email = null;

        if ("kakao".equals(registrationId)) {
            // kakao: nested structure
            providerId = String.valueOf(attributes.get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            name = (String) profile.get("nickname");
            if (kakaoAccount.containsKey("email")) {
                email = (String) kakaoAccount.get("email");
            }
        } else {
            // google or other
            providerId = (String) attributes.get("sub");
            name = (String) attributes.get("name");
            email = (String) attributes.get("email");
        }

        if (name == null) {
            throw new IllegalArgumentException("Attribute value for 'name' cannot be null");
        }

        // 회원 정보 저장 or 조회
        Member member = memberRepository.findByProviderAndProviderId(registrationId, providerId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setEmail(email);
                    newMember.setName(name);
                    newMember.setProvider(registrationId);
                    newMember.setProviderId(providerId);
                    return memberRepository.save(newMember);
                });

        extractedAttributes.put("name", name);
        extractedAttributes.put("providerId", providerId);
        extractedAttributes.put("provider", registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                extractedAttributes,
                "name" // 사용자의 이름 속성을 식별자로 지정
        );
    }
}
