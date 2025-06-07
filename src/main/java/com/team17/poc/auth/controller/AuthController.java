package com.team17.poc.auth.controller;

import com.team17.poc.auth.dto.LoginRequest;
import com.team17.poc.auth.dto.SignupRequest;
import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름을 입력받아 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setName(request.getName());
        member.setProvider("local");

        memberRepository.save(member);
        // return ResponseEntity.ok("회원가입 완료");

        return ResponseEntity.ok(
                Map.of(
                        "message", "회원가입 완료",
                        "id", member.getId(),
                        "name", member.getName(),
                        "email", member.getEmail()
                )
        );
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 세션을 생성합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Member> optionalMember = memberRepository.findByEmail(request.getEmail());

        if (optionalMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 사용자입니다.");
        }

        Member member = optionalMember.get();

        if (!"local".equals(member.getProvider())) {
            return ResponseEntity.badRequest().body("소셜 로그인 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("memberId", member.getId());
        // return ResponseEntity.ok("로그인 성공");
        return ResponseEntity.ok(
                Map.of(
                        "message", "로그인 성공",
                        "id", member.getId(),
                        "name", member.getName(),
                        "email", member.getEmail()
                )
        );
    }

    @Operation(summary = "로그아웃", description = "세션을 만료시켜 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
