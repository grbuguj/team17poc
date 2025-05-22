package com.team17.poc.auth.repository;

import com.team17.poc.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    Optional<Member> findByEmail(String email);
}
