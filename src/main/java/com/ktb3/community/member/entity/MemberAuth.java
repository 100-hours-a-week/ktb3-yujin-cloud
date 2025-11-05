package com.ktb3.community.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 회원인증 엔티티
 * 관계 설계 : Member : MemberAuth = 1:1 (단방향)
 *  → 정보수정, 비번 페이지가 따로 존재하기도 하고 처음 insert가 아닌 이상 두테이블이 동시에 바뀔일이 거의 없다고 생각했음
 */
@Entity
@Table(name="member_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemberAuth {

    @Id
    @Column(name = "member_id")
    private Long id;
    @Column(nullable = false, length = 255)
    private String password;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    // 단방향
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // pk와 fk를 같은값으로 사용한다는 뜻
    @JoinColumn(name="member_id")
    private Member member;

    @Builder
    public MemberAuth(Member member, String password) {
        this.member = member;
        this.password = password;
    }

    public void changePassword(String password) {
        this.password = password;
    }

}
