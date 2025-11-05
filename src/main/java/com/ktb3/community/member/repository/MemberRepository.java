package com.ktb3.community.member.repository;
import com.ktb3.community.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

}
