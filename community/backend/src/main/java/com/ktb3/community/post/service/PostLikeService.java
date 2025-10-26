package com.ktb3.community.post.service;

import com.ktb3.community.common.exception.BusinessException;
import com.ktb3.community.file.service.FileService;
import com.ktb3.community.member.entity.Member;
import com.ktb3.community.member.repository.MemberRepository;
import com.ktb3.community.post.dto.PostCommentDto;
import com.ktb3.community.post.dto.PostLikeDto;
import com.ktb3.community.post.entity.Post;
import com.ktb3.community.post.entity.PostComment;
import com.ktb3.community.post.entity.PostLike;
import com.ktb3.community.post.entity.PostLikeId;
import com.ktb3.community.post.repository.PostCommentRepository;
import com.ktb3.community.post.repository.PostLikeRepository;
import com.ktb3.community.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository likeRepository;

    @Transactional
    public PostLikeDto.LikeResponse changeLikeState(Long postId, Long memberId) {

        // 1. 게시물 존재 확인
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST,"존재하지 않는 게시물입니다."));

        // 2. 회원 존재 확인
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(()-> new BusinessException(HttpStatus.BAD_REQUEST,"존재하지 않는 회원입니다."));

        // 3. 현재 좋아요 상태 확인
        PostLikeId likeId = new PostLikeId(memberId, postId);
        boolean currentLiked = likeRepository.existsById(likeId);

        // 4. 상태에 따른 좋아요 생성/취소
        boolean nowLiked;
        // 좋아요 취소
        if(currentLiked) {
            likeRepository.deleteById(likeId);
            nowLiked = false;
        }else { // 좋아요 생성
            PostLike like = new PostLike(member, post);
            likeRepository.save(like);
            nowLiked = true;
        }

        // 5. 현재 좋아요 개수
        long likeCount = likeRepository.countByPostId(postId);

        return new PostLikeDto.LikeResponse(nowLiked,likeCount);

    }
}
