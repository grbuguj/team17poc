package com.team17.poc.auth.controller;

import com.team17.poc.auth.dto.LoginRequest;
import com.team17.poc.auth.dto.SignupRequest;
import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.model.CustomUserPrincipal;
import com.team17.poc.auth.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "리다이렉트 URL 설정", description = "로그인 성공 후 리다이렉트할 URL을 세션에 저장합니다.")
    @PostMapping("/set-redirect")
    public ResponseEntity<?> setRedirectUrl(@RequestParam String redirectUrl, HttpSession session) {
        session.setAttribute("redirectUrl", redirectUrl);
        return ResponseEntity.ok(Map.of("message", "redirectUrl 저장 완료"));
    }


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
        session.setAttribute("memberName", member.getName());

        CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(member);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserPrincipal, null, customUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(
                Map.of(
                        "message", "로그인 성공",
                        "id", member.getId(),
                        "name", member.getName(),
                        "email", member.getEmail()
                )
        );
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 세션과 인증 정보를 모두 초기화하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    @Operation(summary = "세션 확인", description = "현재 세션 기반 로그인 상태를 확인합니다. 로그인된 경우 사용자 ID와 이름을 반환합니다.")
    @GetMapping("/session-check")
    public ResponseEntity<Map<String, Object>> sessionCheck(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);

        if (!isAuthenticated) {
            return ResponseEntity.ok(Map.of(
                    "loginStatus", false,
                    "message", "인증 안됨"
            ));
        }

        Long memberId = (Long) session.getAttribute("memberId"); // local 로그인용
        String memberName = (String) session.getAttribute("memberName");

        // OAuth 로그인 사용자는 Principal 객체에서 정보 꺼내기
        Object principal = authentication.getPrincipal();
        String oauthName = (principal instanceof org.springframework.security.core.userdetails.User)
                ? ((org.springframework.security.core.userdetails.User) principal).getUsername()
                : principal.toString();

        return ResponseEntity.ok(Map.of(
                "loginStatus", true,
                "id", memberId != null ? memberId : null,
                "name", memberName != null ? memberName : oauthName
        ));
    }


    // 추가 - 마이페이지 인증 관련
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // provider 값으로 로컬/소셜 구분
        boolean isSocial = !"local".equalsIgnoreCase(member.getProvider());
        String provider = Optional.ofNullable(member.getProvider()).orElse("local");

        // ✅ name null-safe 처리
        String name = Optional.ofNullable(member.getName()).orElse("이름없음");

        return ResponseEntity.ok(Map.of(
                "email", member.getEmail(),
                "name", name,
                "isSocial", isSocial
        ));
    }



}
