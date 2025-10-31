package com.ktb3.community.file.repository;

import com.ktb3.community.file.entity.File;
import com.ktb3.community.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // 회원의 프로필 이미지 조회
    @Query("SELECT f FROM File f " +
            "WHERE f.member.id = :memberId " +
            "AND f.type = 'profile' " +
            "AND f.deletedAt IS NULL")
    Optional<File> findProfileByMemberId(@Param("memberId") Long memberId);

    // 게시물 이미지 조회(삭제되지 않은 것만, 순서대로)
    List<File> findByPost_IdAndDeletedAtIsNullOrderByFileOrderAsc(Long postId);

    // 게시물의 파일 개수 조회
    int countByPost_IdAndDeletedAtIsNull(Long postId);

    // 여러 회원의 특정 타입 파일 조회
    List<File> findByMember_IdInAndTypeAndDeletedAtIsNull(List<Long> memberIds, String type);


}
