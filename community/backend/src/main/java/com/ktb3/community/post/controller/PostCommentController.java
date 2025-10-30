package com.ktb3.community.post.controller;

import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.post.dto.PostCommentDto;
import com.ktb3.community.post.service.PostCommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService commentService;

    /**
     * 댓글 리스트
     * @param postId
     * @param page
     * @param size
     * @param memberId
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<PostCommentDto.CommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthMemberId Long memberId) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostCommentDto.CommentResponse> comments =
                commentService.getComments(postId, memberId, pageable);

        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 생성
     * @param postId
     * @param request
     * @param memberId
     * @return
     */
    @PostMapping
    public ResponseEntity<PostCommentDto.CommentResponse> createComment(
            @PathVariable Long postId, @Valid @RequestBody PostCommentDto.CommentRequest request,
            @AuthMemberId Long memberId) {

        // 댓글 생성
        PostCommentDto.CommentResponse response = commentService.createComment(postId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     * @param postId
     * @param commentId
     * @param request
     * @param memberId
     * @return
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<PostCommentDto.CommentResponse> updateComment(
            @PathVariable Long postId, @PathVariable Long commentId,
            @Valid @RequestBody PostCommentDto.CommentRequest request,
            @AuthMemberId Long memberId) {

        // 댓글 수정
        PostCommentDto.CommentResponse response = commentService.updateComment(commentId, memberId, request);

        return ResponseEntity.ok(response);

    }

    /**
     * 댓글 삭제
     * @param postId
     * @param commentId
     * @param memberId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long postId, @PathVariable Long commentId, @AuthMemberId Long memberId) {

        // 댓글 삭제 - 하드 딜리트
        commentService.deleteComment(commentId, memberId);

        return ResponseEntity.ok(Map.of("message", "댓글이 삭제되었습니다."));

    }


}
