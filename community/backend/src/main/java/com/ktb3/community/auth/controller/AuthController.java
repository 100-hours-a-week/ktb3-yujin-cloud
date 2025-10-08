package com.ktb3.community.auth.controller;

import com.ktb3.community.auth.dto.AuthDto;
import com.ktb3.community.auth.service.AuthService;
import com.ktb3.community.member.dto.MemberDto;
import com.ktb3.community.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping
    public ResponseEntity<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request, HttpSession session) {

        AuthDto.LoginResponse response = authService.login(request);

        // 세션에 회원아이디값 저장 - jwt방식과 유사하게 하기위함
        session.setAttribute("memberId", response.getId());
        // 30분 유지
//        session.setMaxInactiveInterval(60 * 30);
        System.out.println("세션 생성됨: " + session.getId());

        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @DeleteMapping
    public ResponseEntity<String> logout(HttpSession session) {
        System.out.println("로그아웃 세션ID: " + session.getId());
        // 세션 삭제
        session.invalidate();
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody AuthDto.ChangePasswordRequest request,
            HttpSession session){

        // 1. 로그인 확인
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        // 2. 새 비밀번호 일치 확인
        if(!request.isNewPasswordMatching()) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호 변경
        authService.changePassword(memberId, request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");

    }





}
