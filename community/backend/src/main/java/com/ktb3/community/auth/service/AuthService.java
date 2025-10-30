package com.ktb3.community.auth.service;

import com.ktb3.community.auth.dto.AuthDto;
import com.ktb3.community.auth.entity.RefreshToken;
import com.ktb3.community.auth.repository.RefreshTokenRepository;
import com.ktb3.community.common.exception.BusinessException;
import com.ktb3.community.common.util.JwtProvider;
import com.ktb3.community.member.entity.Member;
import com.ktb3.community.member.entity.MemberAuth;
import com.ktb3.community.member.repository.MemberAuthRepository;
import com.ktb3.community.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberAuthRepository memberAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthDto.TokenResponse login(AuthDto.LoginRequest request){

        // 1. 이메일로 회원조회
        Member member = memberRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다."));

        // 2. 해당 회원의 비밀번호 조회
        MemberAuth memberAuth = memberAuthRepository.findById(member.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "회원 인증정보를 찾을 수 없습니다."));

        // 3. 2번의 값과 입력값 일치하는지 확인
        if (!passwordEncoder.matches(request.getPassword(), memberAuth.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        // 4. 기존 리프레시 토큰 무효화 - 보안상의 이유로 ‘중복 로그인 방지’ 및 ‘토큰 탈취 대응'을 위함
        refreshTokenRepository.deleteByMemberId(member.getId());

        // 5. 토큰 발급
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getNickname());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // 6. RT DB에 저장
        // refreshToken 안의 payload(Claims)에서 만료시간(exp)을 꺼내 localDateTime형식으로 DB에 저장하기 위함
        Claims rtClaims = Jwts.parserBuilder().setSigningKey(jwtProvider.getKey()).build().parseClaimsJws(refreshToken).getBody();
        LocalDateTime expiresAt = LocalDateTime.ofInstant(rtClaims.getExpiration().toInstant(), ZoneId.systemDefault());
        refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken, expiresAt));


        return new AuthDto.TokenResponse(
                member.getId(), member.getEmail(), member.getNickname(),
                accessToken,
                refreshToken
        );

    }

    public AuthDto.TokenResponse refresh(HttpServletRequest request){

        // 1. 쿠키에서 리프레시 토큰 추출
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token이 없습니다.");
        }

        // 2. 리프레스 토큰 유효성 검사
        if (!jwtProvider.validate(refreshToken)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
        }

        Claims claims = jwtProvider.claims(refreshToken);
        Long memberId = claims.get("id", Long.class);

        // 3. DB에서 Refresh Token 확인F
        RefreshToken saved = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "저장된 Refresh Token이 없습니다."));

        // 4. DB의 담긴 값과 request로 넘어온 값 비교
        if (!saved.getToken().equals(refreshToken)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Refresh Token이 일치하지 않습니다.");
        }

        // 5. 새 Access / Refresh Token 생성 (회전)
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "회원 정보를 찾을 수 없습니다."));

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getNickname());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());


        // 6. DB에서 기존 refreshToken 무효화(회전 - 절대만료)
        saved.updateToken(newRefreshToken);
        refreshTokenRepository.save(saved);

        return new AuthDto.TokenResponse(member.getId(), member.getEmail(), member.getNickname(), newAccessToken, newRefreshToken);

    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken == null) {
            return; // 쿠키가 이미 없으면 그냥 종료
        }
        Claims claims = jwtProvider.claims(refreshToken);
        Long memberId = claims.get("id", Long.class);

        // DB에서 RefreshToken 삭제 (DB에서)
        refreshTokenRepository.deleteByMemberId(memberId);

    }

    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {

        // 1. 회원 조회
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST,"회원을 찾을 수 없습니다."));

        // 2. 인증정보 조회
        MemberAuth memberAuth = memberAuthRepository.findById(memberId)
                .orElseThrow(()-> new BusinessException(HttpStatus.UNAUTHORIZED, "인증정보를 찾을 수 없습니다."));

        // 3. 현재 비밀번호 맞는지 확인
        if(!passwordEncoder.matches(currentPassword, memberAuth.getPassword())){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일지하지 않습니다.");
        }

        // 4. 새로운 비밀번호랑 현재 비밀번호가 다른지 확인
        if (passwordEncoder.matches(newPassword, memberAuth.getPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "새 비밀번호는 현재 비밀번호와 달라야 합니다");
        }

        // 5. 새로운 비밀번호 암호화하여 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        memberAuth.changePassword(encodedPassword);
    }

}
