package com.ktb3.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb3.community.post.dto.PostDto;
import com.ktb3.community.post.entity.Post;
import com.ktb3.community.post.service.PostService;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시물 목록 조회 - 커서 페이징
     * @param cursor
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<PostDto.PostListPageResponse> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size) {

        // 최대 크기 제한
        if (size > 100) {
            size = 100;
        }

        PostDto.PostListPageResponse response = postService.getPostList(cursor, size);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 상세 조회
     * @param postId
     * @param session
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            HttpSession session) {

        // 1. 현재 로그인한 회원 ID 조회
        Long currentMemberId = getMemberIdFromSession(session);

        // 2. 게시물 상세 조회
        PostDto.PostDetailResponse response = postService.getPostDetail(postId, currentMemberId);

        return ResponseEntity.ok(response);
    }



    /**
     *본인 로그인 확인
     */
    private Long getMemberIdFromSession(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        return memberId;
    }

    /**
     * 게시물 등록
     * @param requestJson
     * @param images
     * @param session
     * @return
     * @throws IOException
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto.PostResponse> createPost(
            @RequestParam("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpSession session) throws IOException {

        Long memberId = getMemberIdFromSession(session);

        // 문자열을 DTO로 직접 변환
        ObjectMapper mapper = new ObjectMapper();
        PostDto.PostCreateRequest request = mapper.readValue(requestJson, PostDto.PostCreateRequest.class);

        PostDto.PostResponse response = postService.createPost(memberId, request, images);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 수정
     * @param postId
     * @param request
     * @param session
     * @return
     * @throws IOException
     */
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto.PostResponse> updatePost(
            @PathVariable Long postId, @Valid @ModelAttribute PostDto.PostUpdateRequest request,
            HttpSession session) throws IOException{

        // 회원 조회
        Long memberId = getMemberIdFromSession(session);

        PostDto.PostResponse response = postService.updatePost(postId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 삭제
     * @param postId
     * @param session
     * @return
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpSession session) {

        // 회원 조회
        Long memberId = getMemberIdFromSession(session);

        postService.deletePost(postId, memberId);

        return ResponseEntity.ok("게시물이 삭제되었습니다.");

    }


}
