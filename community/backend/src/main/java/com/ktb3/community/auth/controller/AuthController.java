package com.ktb3.community.auth.controller;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.auth.dto.AuthDto;
import com.ktb3.community.auth.service.AuthService;
import com.ktb3.community.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.TokenResponse tr = authService.login(request);

        // Access Token 쿠키로
        ResponseCookie atCookie = ResponseCookie.from("accessToken", tr.getAccessToken())
                .httpOnly(true)
                .secure(false)     // 개발 중엔 false (HTTP 가능)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Lax")
                .build();

        // Refresh Token 쿠키로
        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", tr.getRefreshToken())
                .httpOnly(true)
                .secure(false)     // 개발 중엔 false (HTTP 가능)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        // 유저 정보는 JSON 응답으로
        Map<String, Object> body = Map.of(
                "id", tr.getId(),
                "email", tr.getEmail(),
                "nickname", tr.getNickname()
        );

        return ResponseEntity.ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, atCookie.toString());
                    h.add(HttpHeaders.SET_COOKIE, rtCookie.toString());
                })
                .body(body);
    }

    // access 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {

        AuthDto.TokenResponse tr = authService.refresh(request);

        // 새 Access / Refresh 쿠키 발급
        ResponseCookie atCookie = ResponseCookie.from("accessToken", tr.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Lax")
                .build();

        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", tr.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, atCookie.toString());
                    h.add(HttpHeaders.SET_COOKIE, rtCookie.toString());
                })
                .body(Map.of("message", "토큰 재발급 성공"));
    }

    // 로그아웃
    @DeleteMapping
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        authService.logout(request);

        // AccessToken & RefreshToken 쿠키 삭제
        ResponseCookie atCookie = ResponseCookie.from("accessToken", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        ResponseCookie rtCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        Map<String, String> body = Map.of("message", "로그아웃 완료");

        return ResponseEntity.ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, atCookie.toString());
                    h.add(HttpHeaders.SET_COOKIE, rtCookie.toString());
                })
                .body(body);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>>  changePassword(
            @Valid @RequestBody AuthDto.ChangePasswordRequest request,
            @AuthMemberId Long memberId){

        // 1. 새 비밀번호 일치 확인
        if(!request.isNewPasswordMatching()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "새 비밀번호가 일치하지 않습니다.");
        }

        // 2. 비밀번호 변경
        authService.changePassword(memberId, request.getCurrentPassword(), request.getNewPassword());

        return ResponseEntity.ok(Map.of("message", "비밀번호 변경이 완료되었습니다."));

    }





}
