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
 * 게시물 엔티티
 * 관계 설계 : Member : Post = 1:N (단방향)
 *  → 회원쪽에서는 항상 게시물 조회할 필요없음
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    @Column(nullable = false)
    private long hit = 0L;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 단방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id",nullable = false)
    private Member member;

    @Builder
    public Post(Member member, String title, String content){
        this.member = member;
        this.title = title;
        this.content = content;
        this.hit = 0;
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // TODO. 대규모 일때는 조회수 관리 어떻게 할건지 고민필요(동시성 이슈) - 근데 엄청 자세하게 보여줄필요는 없지 않나?
    public void increaseHit() {
        this.hit ++;
    }

    public void deletePost() {
        this.deletedAt = LocalDateTime.now();
    }

    // TODO. post, postComment에 작성자 확인 함수있는데, 굳이 따로 안두고 같이 하나에서 써도 되지않나?
    public boolean isOwner(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}
