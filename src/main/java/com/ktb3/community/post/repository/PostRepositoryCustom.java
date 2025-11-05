package com.ktb3.community.post.repository;

import com.ktb3.community.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    /**
     * 커서 기반 게시물 목록 조회
     * @param cursor - 이전 페이지의 마지막 게시물 ID
     * @param size - 조회할 개수
     * @return - 게시물 목록 (Member fetch join 포함)
     */
    List<Post> findPostsByCursor(Long cursor, int size);
}
