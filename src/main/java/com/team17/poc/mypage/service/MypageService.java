package com.team17.poc.mypage.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.repository.MemberRepository;
import com.team17.poc.mypage.dto.ChangePasswordRequest;
import com.team17.poc.mypage.dto.UpdateProfileRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MypageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void updateEmail(Long memberId, String newEmail) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!"local".equals(member.getProvider())) {
            throw new IllegalStateException("소셜 로그인 사용자는 이메일을 변경할 수 없습니다.");
        }

        member.setEmail(newEmail);
    }

    public void updateName(Long memberId, String newName) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        member.setName(newName);
        memberRepository.save(member);

        System.out.println("== 이름 변경 시도 ==");
        System.out.println("memberId = " + memberId);
        System.out.println("newName = " + newName);
        System.out.println("member.name(before) = " + member.getName());
    }


    public void changePassword(Long memberId, ChangePasswordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!"local".equals(member.getProvider())) {
            throw new IllegalStateException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 틀립니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

}
