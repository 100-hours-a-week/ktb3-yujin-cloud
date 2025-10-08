package com.ktb3.community.file.repository;

import com.ktb3.community.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    // 회원의 프로필 이미지 조회
    @Query("SELECT f FROM File f " +
            "WHERE f.member.id = :memberId " +
            "AND f.type = 'profile' " +
            "AND f.deletedAt IS NULL")
    Optional<File> findProfileByMemberId(@Param("memberId") Long memberId);
}
