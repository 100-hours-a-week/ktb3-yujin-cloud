package com.ktb3.community.post.repository;

import com.ktb3.community.post.entity.Post;
import com.ktb3.community.post.entity.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostsByCursor(Long cursor, int size) {
        return queryFactory
                .selectFrom(QPost.post)
                .join(QPost.post.member).fetchJoin()  // Member JOIN FETCH
                .where(
                        QPost.post.deletedAt.isNull(),  // 삭제되지 않은 게시물
                        cursor != null ? QPost.post.id.lt(cursor) : null // null이면 조건 없음 (첫 페이지)
                )
                .orderBy(QPost.post.id.desc())  // ID 내림차순
                .limit(size + 1)  // size+1 조회 (hasNext 판단용)
                .fetch();
    }


}
