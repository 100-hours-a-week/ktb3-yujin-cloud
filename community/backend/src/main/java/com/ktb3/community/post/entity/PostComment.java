package com.ktb3.community.post.entity;

import com.ktb3.community.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 게시물 댓글 엔티티
 * 관계 설계
 * 1. Member : PostComment = 1:N (단방향)
 *  → 회원쪽에서는 항상 게시물댓글 조회할 필요없음
 * 2. Post : PostComment = 1:N (단방향)
 *  → Post에서 댓글 목록은 Repository 쿼리로 조회
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;
    @Column(nullable = false)
    private String comment;

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    // 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    // 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @Builder
    public PostComment(Member member, Post post, String comment) {
        this.member = member;
        this.post = post;
        this.comment = comment;
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void deleteComment() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isOwner(Long memberId) {
        return this.member.getId().equals(memberId);
    }


}
