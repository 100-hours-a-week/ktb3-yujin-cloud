package com.ktb3.community.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessExpMs;
    private final long refreshExpMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-ms}") long accessExpMs,
            @Value("${jwt.refresh-exp-ms}") long refreshExpMs
    ) {
        byte[] bytes = (isBase64(secret) ? Base64.getDecoder().decode(secret) : secret.getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(bytes);
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    public String createAccessToken(Long memberId, String email, String nickname) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) // 토큰의 주체로 어떤 사용자인지 식별하기 위한 ID를 담음
                .setIssuedAt(now) // 발급시간
                .setExpiration(new Date(now.getTime() + accessExpMs)) // 만료시간
                .claim("id", memberId)
                .claim("email", email)
                .claim("nickname", nickname)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpMs))
                .claim("id", memberId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // TODO. 이 만료검증이 DB의 값이 되어여할거 같은데?
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims claims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private boolean isBase64(String s) {
        try { Base64.getDecoder().decode(s); return true; } catch (IllegalArgumentException e) { return false; }
    }

    public Key getKey() {
        return key;
    }
}
