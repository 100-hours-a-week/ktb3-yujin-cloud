package com.ktb3.community.post.controller;

import com.ktb3.community.post.dto.PostCommentDto;
import com.ktb3.community.post.dto.PostDto;
import com.ktb3.community.post.service.PostCommentService;
import com.ktb3.community.post.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.Comment;
import java.io.IOException;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService commentService;

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
     * 댓글 생성
     * @param postId
     * @param request
     * @param session
     * @return
     */
    @PostMapping
    public ResponseEntity<PostCommentDto.CommentResponse> createComment(
            @PathVariable Long postId, @Valid @RequestBody PostCommentDto.CommentRequest request,
            HttpSession session) {

        // 1. 로그인 확인
        Long memberId = getMemberIdFromSession(session);

        // 2. 댓글 생성
        PostCommentDto.CommentResponse response = commentService.createComment(postId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     * @param postId
     * @param commentId
     * @param request
     * @param session
     * @return
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<PostCommentDto.CommentResponse> updateComment(
            @PathVariable Long postId, @PathVariable Long commentId,
            @Valid @RequestBody PostCommentDto.CommentRequest request,
            HttpSession session) {

        // 1. 로그인 확인
        Long memberId = getMemberIdFromSession(session);

        // 2. 댓글 수정
        PostCommentDto.CommentResponse response = commentService.updateComment(commentId, memberId, request);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpSession session) {

        // 1. 로그인 확인
        Long memberId = getMemberIdFromSession(session);

        // 2. 댓글 삭제 - 하드 딜리트
        commentService.deleteComment(commentId, memberId);

        return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");

    }


}
