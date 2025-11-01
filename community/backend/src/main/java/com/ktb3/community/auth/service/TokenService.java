package com.ktb3.community.auth.service;

import com.ktb3.community.auth.entity.RefreshToken;
import com.ktb3.community.auth.repository.RefreshTokenRepository;
import com.ktb3.community.common.exception.BusinessException;
import com.ktb3.community.common.util.JwtProvider;
import com.ktb3.community.member.entity.Member;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Access Token과 Refresh Token을 생성하고 DB에 저장
     * @param member
     * @return
     */
    @Transactional
    public TokenInfo createTokens(Member member) {
        // 1. 토큰 생성
        String accessToken = jwtProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getNickname()
        );
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // 2. Refresh Token DB에 저장
        saveRefreshToken(member.getId(), refreshToken);

        return new TokenInfo(accessToken, refreshToken);
    }

    /**
     * Refresh Token을 DB에 저장
     * @param memberId
     * @param refreshToken
     */
    @Transactional
    public void saveRefreshToken(Long memberId, String refreshToken) {
        // 만료시간 추출
        Claims claims = jwtProvider.claims(refreshToken);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                claims.getExpiration().toInstant(),
                ZoneId.systemDefault()
        );

        // 기존 토큰 무효화 후 새로 저장 - 보안상의 이유로 ‘중복 로그인 방지’ 및 ‘토큰 탈취 대응'을 위함
        refreshTokenRepository.deleteByMemberId(memberId);
        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken, expiresAt));
    }

    /**
     * Request에서 Refresh Token 추출
     * @param request
     * @return
     */
    public String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Refresh Token 검증
     * @param refreshToken
     * @return
     */
    public RefreshToken validateRefreshToken(String refreshToken) {

        // 1. 토큰 형식 및 만료 검증
        if (!jwtProvider.validate(refreshToken)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
        }

        // 2. memberId 추출
        Claims claims = jwtProvider.claims(refreshToken);
        Long memberId = claims.get("id", Long.class);

        // 3. DB에서 토큰 확인
        RefreshToken saved = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "저장된 Refresh Token이 없습니다."));

        // 4. DB 값과 비교
        if (!saved.getToken().equals(refreshToken)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다.");
        }

        return saved;
    }

    /**
     * Refresh Token 회전 - 보안 강화
     * @param savedToken
     * @param member
     * @return
     */
    @Transactional
    public TokenInfo rotateTokens(RefreshToken savedToken, Member member) {
        // 새 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(
                member.getId(),
                member.getEmail(),
                member.getNickname()
        );
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());

        // DB 업데이트 (기존 토큰 무효화)
        savedToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return new TokenInfo(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    public record TokenInfo(String accessToken, String refreshToken) {}
}
