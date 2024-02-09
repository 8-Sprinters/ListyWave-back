package com.listywave.list.repository;

import static com.listywave.list.application.domain.QComment.comment;
import static com.listywave.list.application.domain.QLists.lists;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getComments(Lists list, int size, Long cursorId) {
        return queryFactory.selectFrom(comment)
                .join(comment.list, lists)
                .where(
                        comment.list.eq(list),
                        comment.id.lt(cursorId)
                )
                .orderBy(comment.id.desc())
                .limit(size + 1)
                .fetch();
    }
}
