package com.ktb3.community.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ktb3.community.post.entity.PostComment;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostCommentDto {

    @Getter
    @NoArgsConstructor
    public static class CommentRequest {

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        private String comment;
    }

    @Getter
    @Builder
    public static class CommentResponse {

        private Long commentId;
        private Long postId;
        private String comment;

        // 작성자 정보
        private Long authorId;
        private String authorNickname;
        private String authorProfileImageUrl;

        private final LocalDateTime createdAt;

        // 권한
        private final boolean isAuthor;

        public static CommentResponse from(PostComment comment, String profileImageUrl, Long currentMemberId) {

            // 내가 작성한 댓글인지 확인
            boolean isAuthor = currentMemberId != null
                    && comment.getMember().getId().equals(currentMemberId);

            return CommentResponse.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPost().getId())
                    .comment(comment.getComment())
                    .authorId(comment.getMember().getId())
                    .authorNickname(comment.getMember().getNickname())
                    .authorProfileImageUrl(profileImageUrl)
                    .createdAt(comment.getCreatedAt())
                    .isAuthor(isAuthor)
                    .build();
        }

    }
}
