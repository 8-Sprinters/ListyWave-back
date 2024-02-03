package com.listywave.list.repository;

import static com.listywave.list.application.domain.QComment.comment;
import static com.listywave.list.application.domain.QLists.lists;

import com.listywave.list.application.domain.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getComments(Long listId, int size, Long cursorId) {
        return queryFactory.selectFrom(comment)
                .join(comment.list, lists)
                .where(comment.list.id.eq(listId))
                .orderBy(comment.id.asc())
                .limit(size)
                .offset(cursorId + 1)
                .fetch();
    }
}
