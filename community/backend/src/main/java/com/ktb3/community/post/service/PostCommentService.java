package com.ktb3.community.post.service;

import com.ktb3.community.file.service.FileService;
import com.ktb3.community.member.entity.Member;
import com.ktb3.community.member.repository.MemberRepository;
import com.ktb3.community.post.dto.PostCommentDto;
import com.ktb3.community.post.entity.Post;
import com.ktb3.community.post.entity.PostComment;
import com.ktb3.community.post.repository.PostCommentRepository;
import com.ktb3.community.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    /**
     *
     * @param postId
     * @param currentMemberId
     * @param pageable
     * @return
     */
    public Page<PostCommentDto.CommentResponse> getComments(Long postId,
                                                            Long currentMemberId,
                                                            Pageable pageable) {

        // 1. 댓글 목록 조회 (Member JOIN FETCH)
        Page<PostComment> comments = commentRepository.findCommentsByPostId(postId, pageable);

        if (comments.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. 작성자 ID 목록 추출
        List<Long> memberIds = comments.getContent().stream()
                .map(c -> c.getMember().getId())
                .distinct()
                .collect(Collectors.toList());

        // 3. 프로필 이미지 배치 조회 (N+1 방지)
        Map<Long, String> profileUrls = fileService.getProfileImageUrls(memberIds);

        // 4. DTO 변환
        return comments.map(comment -> {
            String profileUrl = profileUrls.get(comment.getMember().getId());
            return PostCommentDto.CommentResponse.from(comment, profileUrl, currentMemberId);
        });
    }
    /**
     * 댓글 생성
     * @param postId
     * @param memberId
     * @param request
     * @return
     */
    @Transactional
    public PostCommentDto.CommentResponse createComment(Long postId, Long memberId, PostCommentDto.CommentRequest request) {

        // 1. 존재하는 게시물인지 확인
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        // 2. 작성자 확인
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 3. 댓글 생성 및 저장
        PostComment comment = PostComment.builder()
                .post(post)
                .member(member)
                .comment(request.getComment())
                .build();

        PostComment savedComment = commentRepository.save(comment);

        // 4. 작성자 프로필 이미지 조회
        String profileUrl = fileService.getProfileImageUrl(memberId);

        return PostCommentDto.CommentResponse.from(savedComment,profileUrl,memberId);
    }

    /**
     * 댓글 수정
     * @param commentId
     * @param memberId
     * @param request
     * @return
     */
    @Transactional
    public PostCommentDto.CommentResponse updateComment(Long commentId, Long memberId, PostCommentDto.CommentRequest request) {

        // 1. 댓글 조회
        PostComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 2. 작성자 확인
        validateOwnership(comment,memberId);

        // 3. 댓글 수정
        comment.updateComment(request.getComment());

        // 4. 작성자 프로필 이미지 조회
        String profileUrl = fileService.getProfileImageUrl(memberId);

        return PostCommentDto.CommentResponse.from(comment,profileUrl,memberId);
    }

    /**
     * 댓글 삭제 - 사용자가 삭제하는라 하드 삭제
     * @param commentId
     * @param memberId
     */
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        // 1. 댓글 조회
        PostComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 2. 작성자 확인
        validateOwnership(comment, memberId);

        // 3. 댓글 삭제 - 하드 삭제
        commentRepository.delete(comment);

    }

    /**
     * 댓글 삭제 - 게시물 삭제될때 같이 소프트 삭제
     * @param postId
     */
    @Transactional
    public void softDeleteComments(Long postId) {

        // 1. 해당 게시물의 댓글 리스트 확인
        List<PostComment> comments = commentRepository.findByPost_IdAndDeletedAtIsNull(postId);

        if (comments.isEmpty()) {
            return;
        }

        // 2. 해당 게시물의 모든 댓글 소프트 삭제
        comments.forEach(PostComment::deleteComment);
    }

    // 댓글 작성자 권한 확인
    private void validateOwnership(PostComment comment, Long memberId) {
        if (!comment.isOwner(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
