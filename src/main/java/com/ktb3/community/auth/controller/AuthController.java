package com.ktb3.community.auth.controller;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.auth.dto.AuthDto;
import com.ktb3.community.auth.service.AuthService;
import com.ktb3.community.auth.util.CookieUtil;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    /**
     * 로그인
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthDto.LoginRequest request) {

        AuthDto.TokenResponse response = authService.login(request);

        return createTokenResponse(response);
    }

    /**
     * 토큰 재발급
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {

        AuthDto.TokenResponse response = authService.refresh(request);

        return createTokenResponse(response);
    }

    /**
     * 로그아웃
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {

        authService.logout(request);

        // AccessToken & RefreshToken 쿠키 삭제
        ResponseCookie atCookie = cookieUtil.deleteAccessTokenCookie();
        ResponseCookie rtCookie = cookieUtil.deleteRefreshTokenCookie();

        Map<String, String> body = Map.of("message", "로그아웃 완료");

        return ResponseEntity.ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, atCookie.toString());
                    h.add(HttpHeaders.SET_COOKIE, rtCookie.toString());
                })
                .body(body);
    }

    /**
     * 비밀번호 변경
     * @param request
     * @param memberId
     * @return
     */
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

    /**
     * 토큰 응답 생성 (로그인, 리프레시 공통)
     * @param response
     * @return
     */
    private ResponseEntity<Map<String, Object>> createTokenResponse(AuthDto.TokenResponse response) {
        // 쿠키 생성
        var accessCookie = cookieUtil.createAccessTokenCookie(response.getAccessToken());
        var refreshCookie = cookieUtil.createRefreshTokenCookie(response.getRefreshToken());

        // 응답 Body (토큰은 쿠키로만, 회원정보만 JSON) - 사용안할시 삭제예정
        Map<String, Object> body = Map.of(
                "id", response.getId(),
                "email", response.getEmail(),
                "nickname", response.getNickname()
        );

        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                })
                .body(body);
    }

}
