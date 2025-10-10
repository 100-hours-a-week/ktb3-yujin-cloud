package com.ktb3.community.post.controller;

import com.ktb3.community.post.dto.PostLikeDto;
import com.ktb3.community.post.service.PostLikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService likeService;

    /**
     * 로그인 확인
     */
    private Long getMemberIdFromSession(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        return memberId;
    }

    /**
     * 좋아요 생성/취소
     * @param postId
     * @param session
     * @return
     */
    @PostMapping
    public ResponseEntity<PostLikeDto.LikeResponse> changeLikeState(@PathVariable Long postId, HttpSession session){

        // 1. 로그인 확인
        Long memberId = getMemberIdFromSession(session);

        // 2. 좋아요 생성
        PostLikeDto.LikeResponse response = likeService.changeLikeState(postId, memberId);

        return ResponseEntity.ok(response);
    }

}
