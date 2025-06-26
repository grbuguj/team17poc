package com.team17.poc.auth.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.model.CustomUserPrincipal;
import com.team17.poc.auth.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = request.getClientRegistration().getRegistrationId(); // kakao or google
        String providerId;
        String email;
        String name;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            providerId = attributes.get("id").toString();
            name = (String) profile.get("nickname");
            email = (String) kakaoAccount.get("email");

            if (email == null) {
                // 임시 이메일 생성
                email = "kakao_" + providerId + "@no-email.com";
            }

        } else if ("google".equals(registrationId)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }

        // DB 조회 또는 신규 저장
        final String fRegistrationId = registrationId;
        final String fProviderId = providerId;
        final String fEmail = email;
        final String fName = name;

        Member member = memberRepository.findByProviderAndProviderId(fRegistrationId, fProviderId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setProvider(fRegistrationId);
                    newMember.setProviderId(fProviderId);
                    newMember.setEmail(fEmail);
                    newMember.setName(fName);
                    return memberRepository.save(newMember);
                });

        // 커스텀 attributes 구성 (name이라는 key가 null이면 안 되므로 nickname으로 대체)
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("id", fProviderId);
        customAttributes.put("email", fEmail);
        customAttributes.put("nickname", fName);

        /*
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                customAttributes,
                "nickname"
        );
         */

        // 🔽 추가: 세션에 memberId와 name 저장
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = httpRequest.getSession();
        session.setAttribute("memberId", member.getId());
        session.setAttribute("memberName", member.getName());


        return new CustomUserPrincipal(member, customAttributes);

    }
}
