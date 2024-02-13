package com.listywave.list.repository;

import static com.listywave.list.application.domain.QComment.comment;
import static com.listywave.list.application.domain.QListEntity.listEntity;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.ListEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getComments(ListEntity list, int size, Long cursorId) {
        return queryFactory.selectFrom(comment)
                .join(listEntity).fetchJoin()
                .on(listEntity.id.eq(comment.list.id))
                .where(
                        listEntity.id.eq(list.getId()),
                        comment.id.gt(cursorId)
                )
                .orderBy(comment.id.asc())
                .limit(size + 1)
                .fetch();
    }
}
