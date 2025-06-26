package com.team17.poc.mypage.controller;

import com.team17.poc.mypage.dto.ChangePasswordRequest;
import com.team17.poc.mypage.dto.UpdateNameRequest;
import com.team17.poc.mypage.dto.UpdateProfileRequest;
import com.team17.poc.mypage.service.MypageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService myPageService;

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(HttpSession session,
                                         @RequestBody UpdateProfileRequest request) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        myPageService.updateEmail(memberId, request.getNewEmail());
        return ResponseEntity.ok(Map.of("message", "이메일이 변경되었습니다."));
    }

    @PutMapping("/name")
    public ResponseEntity<?> updateName(HttpSession session,
                                        @RequestBody UpdateNameRequest request) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        myPageService.updateName(memberId, request.getNewName());
        return ResponseEntity.ok(Map.of("message", "이름이 변경되었습니다."));
    }




    @PutMapping("/password")
    public ResponseEntity<?> changePassword(HttpSession session,
                                            @RequestBody ChangePasswordRequest request) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        myPageService.changePassword(memberId, request);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }



}
