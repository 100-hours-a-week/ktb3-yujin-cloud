package com.ktb3.community.post.repository;

import com.ktb3.community.post.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment,Long> {

    // 댓글 조회(삭제X)
    Optional<PostComment> findByIdAndDeletedAtIsNull(Long commentId);

    // 게시물의 댓글 조회 (삭제되지 않은 것만)
    List<PostComment> findByPost_IdAndDeletedAtIsNull(Long postId);

    // 여러 게시물의 댓글 개수 조회 (N+1 방지) - 게시물 목록 조회용
    @Query("SELECT c.post.id as postId, COUNT(c) as commentCount " +
            "FROM PostComment c " +
            "WHERE c.post.id IN :postIds " +
            "AND c.deletedAt IS NULL " +
            "GROUP BY c.post.id")
    List<Map<String, Object>> countByPostIdIn(@Param("postIds") List<Long> postIds);

    // 게시물의 댓글 목록 조회 (페이징, 최신순) - 게시물 상세 조회용
    @Query(value = "SELECT c FROM PostComment c " +
            "JOIN FETCH c.member " +
            "WHERE c.post.id = :postId " +
            "AND c.deletedAt IS NULL " +
            "ORDER BY c.createdAt DESC",
            countQuery = "SELECT COUNT(c) FROM PostComment c " +
                    "WHERE c.post.id = :postId " +
                    "AND c.deletedAt IS NULL")
    Page<PostComment> findCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // 게시물의 댓글 개수 - 게시물 상세용
    long countByPost_IdAndDeletedAtIsNull(Long postId);
}
