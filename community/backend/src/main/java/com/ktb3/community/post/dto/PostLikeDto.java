package com.ktb3.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PostLikeDto {

    @Getter
    @AllArgsConstructor
    public static class LikeResponse {
        // true = 좋아요, false = 취소
        private boolean liked;
        // 현재 게시물의 좋아요 개수
        private long likeCount;
    }

}
