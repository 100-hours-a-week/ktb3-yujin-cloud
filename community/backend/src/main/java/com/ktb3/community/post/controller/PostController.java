package com.ktb3.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktb3.community.auth.annotation.AuthMemberId;
import com.ktb3.community.post.dto.PostDto;
import com.ktb3.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * @param memberId
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto.PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthMemberId Long memberId) {

        // 게시물 상세 조회
        PostDto.PostDetailResponse response = postService.getPostDetail(postId, memberId);

        return ResponseEntity.ok(response);
    }


    /**
     * 게시물 등록
     * @param requestJson
     * @param images
     * @param memberId
     * @return
     * @throws IOException
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto.PostResponse> createPost(
            @RequestParam("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthMemberId Long memberId) throws IOException {

        // 문자열을 DTO로 직접 변환
        ObjectMapper mapper = new ObjectMapper();
        PostDto.PostCreateRequest postRequest = mapper.readValue(requestJson, PostDto.PostCreateRequest.class);

        PostDto.PostResponse postResponse = postService.createPost(memberId, postRequest, images);
        return ResponseEntity.ok(postResponse);
    }

    /**
     * 게시물 수정용 상세 불러오기
     * @param postId
     * @param memberId
     * @return
     */
    @GetMapping("/edit/{postId}")
    public ResponseEntity<PostDto.PostResponse> getPostForEdit(
            @PathVariable Long postId,
            @AuthMemberId Long memberId) {

        PostDto.PostResponse response = postService.getPostForEdit(postId, memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 수정
     * @param postId
     * @param requestJson
     * @param images
     * @param memberId
     * @return
     * @throws IOException
     */
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto.PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthMemberId Long memberId) throws IOException{


        // 문자열을 DTO로 직접 변환
        ObjectMapper mapper = new ObjectMapper();
        PostDto.PostUpdateRequest request = mapper.readValue(requestJson, PostDto.PostUpdateRequest.class);

        PostDto.PostResponse response = postService.updatePost(postId, memberId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 삭제
     * @param postId
     * @param memberId
     * @return
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId, @AuthMemberId Long memberId) {

        postService.deletePost(postId, memberId);

        return ResponseEntity.ok(Map.of("message", "게시물이 삭제되었습니다."));

    }


}
