package com.ktb3.community.auth.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    /**
     * Access Token 쿠키 생성
     * @param token
     * @return
     */
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(ACCESS_TOKEN, token)
                .httpOnly(true)        // JavaScript 접근 차단 (XSS 방어)
                .secure(false)         // 개발: false, 운영: true
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Lax")
                .build();
    }

    /**
     * Refresh Token 쿠키 생성
     * @param token
     * @return
     */
    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
    }

    /**
     * Access Token 쿠키 삭제
     * @return
     */
    public ResponseCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(ACCESS_TOKEN, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .build();
    }

    /**
     * Refresh Token 쿠키 삭제
     * @return
     */
    public ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();
    }

}
