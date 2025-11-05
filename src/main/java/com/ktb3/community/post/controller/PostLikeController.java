package com.ktb3.community.post.controller;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.post.dto.PostLikeDto;
import com.ktb3.community.post.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService likeService;

    /**
     * 좋아요 생성/취소
     * @param postId
     * @param memberId
     * @return
     */
    @PostMapping
    public ResponseEntity<PostLikeDto.LikeResponse> changeLikeState(@PathVariable Long postId, @AuthMemberId Long memberId){

        // 좋아요 생성
        PostLikeDto.LikeResponse response = likeService.changeLikeState(postId, memberId);

        return ResponseEntity.ok(response);
    }

}
