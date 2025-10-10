package com.ktb3.community.post.repository;

import com.ktb3.community.post.entity.PostLike;
import com.ktb3.community.post.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    boolean existsById(PostLikeId id);

    void deleteById(PostLikeId id);

    long countByPostId(Long postId);

    // 여러 게시물의 좋아요 개수 조회 (N+1 방지)
    @Query("SELECT pl.post.id as postId, COUNT(pl) as likeCount " +
            "FROM PostLike pl " +
            "WHERE pl.post.id IN :postIds " +
            "GROUP BY pl.post.id")
    List<Map<String, Object>> countByPostIdIn(@Param("postIds") List<Long> postIds);

    // 특정 회원이 특정 게시물에 좋아요 눌렀나 확인 - 게시물 상세용
    boolean existsByMember_IdAndPost_Id(Long memberId, Long postId);

}
