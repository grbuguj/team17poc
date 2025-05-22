/*
package com.team17.poc.auth.model;

import com.team17.poc.auth.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;

    public CustomUserPrincipal(Member member) {
        this.member = member;
        this.attributes = Collections.emptyMap();
    }

    public CustomUserPrincipal(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Long getId() {
        return member.getId();
    }

    @Override
    public String getName() {
        return member.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

 */


package com.team17.poc.auth.model;

import com.team17.poc.auth.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements OAuth2User, UserDetails {

    private final Member member;
    private final Map<String, Object> attributes;

    public CustomUserPrincipal(Member member) {
        this.member = member;
        this.attributes = null;
    }

    public CustomUserPrincipal(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // ✅ 공통 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // ✅ 일반 로그인용
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ✅ 소셜 로그인용
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getName(); // 또는 member.getId().toString()
    }

    public Long getId() {
        return member.getId();
    }

    public String getEmail() {
        return member.getEmail();
    }

    public String getNameValue() {
        return member.getName();
    }

    public String getProvider() {
        return member.getProvider();
    }
}

