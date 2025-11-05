package com.ktb3.community.post.entity;

import com.ktb3.community.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 개시물 좋아요 엔티티
 * 관계 설계
 * 1. Member : PostLike = 1:N (단방향)
 *  → 회원쪽에서는 항상 좋아요 조회할 필요없음
 * 2. Post : PostLike = 1:N (단방향)
 *  → Post에서 댓글 목록은 Repository 쿼리로 조회
 *  →
 */
@Entity
@Table(name="post_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PostLike {

    // 복합키
    @EmbeddedId
    private PostLikeId id;

    // 회원 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // PostLikeId의 memberId와 매핑
    @JoinColumn(name = "member_id")
    private Member member;

    // 게시물 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId") // PostLikeId의 postId와 매핑
    @JoinColumn(name = "post_id")
    private Post post;

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    // @Builder → 여기서는 오히려 사용하면 자동생성되는 복합키 id가 누락될 수 있음
    public PostLike(Member member, Post post){
        // JPA는 엔티티를 DB에 저장(persist)할 때 PK 필수라 new로 만들어줌
        this.id = new PostLikeId(member.getId(), post.getId());
        this.member = member;
        this.post = post;
    }

}
