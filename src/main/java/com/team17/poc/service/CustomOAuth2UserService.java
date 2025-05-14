package com.team17.poc.service;

import com.team17.poc.entity.Member;
import com.team17.poc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = request.getClientRegistration().getRegistrationId(); // "google"
        String providerId = (String) attributes.get("sub"); // 고유 식별자
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 이미 가입된 회원인지 확인
        Member member = memberRepository.findByProviderAndProviderId(registrationId, providerId)
                .orElseGet(() -> {
                    // 없다면 새로 저장
                    Member newMember = new Member();
                    newMember.setEmail(email);
                    newMember.setName(name);
                    newMember.setProvider(registrationId);
                    newMember.setProviderId(providerId);
                    return memberRepository.save(newMember);
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub"
        );
    }
}
