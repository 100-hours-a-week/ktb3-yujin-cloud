package com.ktb3.community.member.repository;

import com.ktb3.community.member.entity.MemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, Long> {
}
