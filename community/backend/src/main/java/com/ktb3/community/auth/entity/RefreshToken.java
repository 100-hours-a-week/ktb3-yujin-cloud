package com.ktb3.community.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="member_id", nullable = false)
    private Long memberId;

    @Column(name="token", nullable = false, length = 500, unique = true)
    private String token;

    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    public void updateToken(String newToken) {
        this.token = newToken;
    }

    @Builder
    public RefreshToken(Long memberId, String token, LocalDateTime expiresAt) {
        this.memberId = memberId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

}
