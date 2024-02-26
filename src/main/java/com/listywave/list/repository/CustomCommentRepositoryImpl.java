package com.listywave.list.repository;


import static com.listywave.list.application.domain.comment.QComment.comment;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getComments(ListEntity list, int size, Long cursorId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(listEntity).on(comment.list.id.eq(listEntity.id)).fetchJoin()
                .where(
                        comment.list.id.eq(list.getId()),
                        commentIdGt(cursorId),
                        comment.user.isDelete.isFalse()
                )
                .orderBy(comment.id.asc())
                .limit(size + 1)
                .fetch();
    }

    private static BooleanExpression commentIdGt(Long cursorId) {
        if (cursorId == null) {
            return null;
        }
        return comment.id.gt(cursorId);
    }

    @Override
    public Long countByList(ListEntity list) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .leftJoin(listEntity).on(comment.list.id.eq(listEntity.id))
                .where(
                        comment.list.id.eq(list.getId()),
                        comment.isDeleted.isFalse(),
                        comment.user.isDelete.isFalse()
                )
                .fetchOne();
    }
}
