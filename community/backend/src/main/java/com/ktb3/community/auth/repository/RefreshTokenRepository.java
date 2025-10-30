package com.ktb3.community.auth.repository;

import com.ktb3.community.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);
    void deleteByMemberId(Long memberId);
}
