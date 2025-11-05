package com.ktb3.community.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PostLike 복합키 (Embeddable 방식)
 * vs @IdClass : 객체지향적으로 설계해보고 싶었고, 강의에서 다뤘던 개념이라 사용해보고 싶어서 선택함
 *
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode // equals(), hashCode() 자동 재정의
// why? 기본은 메모리 주소 비교인데, 복합키는 필드 값을(memberId,postId) 비교해서 동일한가?를 체크해줘야해서 기본구현을 override해줘야함
public class PostLikeId implements Serializable {

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "post_id")
    private  Long postId;

    // @AllArgsConstructor
    public PostLikeId(Long memberId, Long postId){
        this.memberId = memberId;
        this.postId = postId;
    }
}
