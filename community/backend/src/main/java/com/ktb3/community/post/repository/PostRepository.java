package com.ktb3.community.post.repository;

import com.ktb3.community.post.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {

    // 게시물 조회(삭제X)
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    // 게시물 상세 조회
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.member " +
            "WHERE p.id = :postId " +
            "AND p.deletedAt IS NULL")
    Optional<Post> findByIdWithMember(@Param("postId") Long postId);



}
