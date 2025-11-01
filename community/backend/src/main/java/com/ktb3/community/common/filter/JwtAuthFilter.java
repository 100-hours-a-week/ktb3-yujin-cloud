package com.ktb3.community.common.filter;

import com.ktb3.community.common.util.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 제외 경로는 필터 건너뜀
        if (isExcludedPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        // Access Token 유효성 검증
        if (!jwtProvider.validate(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 유효한 토큰이면 memberId를 request에 저장
        Claims claims = jwtProvider.claims(token);
        Long memberId = claims.get("id", Long.class);
        request.setAttribute("memberId", memberId);

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // 필터 제외 경로 설정 - shouldNotFilter는 분기처리가 불가하여 새로운 함수지정
    private boolean isExcludedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 회원가입 (POST /users)
        if ("/users".equals(path) && "POST".equalsIgnoreCase(method)) return true;


        // 이메일/닉네임 중복확인 (항상 필터 제외)
        if (("/users/email".equals(path) || "/users/nickname".equals(path))
                && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        // 로그인 (POST /auth)
        if ("/auth".equals(path) && "POST".equalsIgnoreCase(method)) return true;


        // 토큰 재발급 (/auth/refresh)
        if ("/auth/refresh".equals(path)) return true;


        // 푸터에 있는 페이지
        if (path.startsWith("/terms") || path.startsWith("/privacy")) return true;


        return false;
    }



}
