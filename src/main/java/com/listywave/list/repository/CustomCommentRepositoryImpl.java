package com.listywave.list.repository;


import static com.listywave.list.application.domain.comment.QComment.comment;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.list.application.domain.reply.QReply.reply;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getComments(ListEntity list, int size, Long cursorId) {
        return queryFactory.selectFrom(comment)
                .join(listEntity).fetchJoin().on(listEntity.id.eq(comment.list.id))
                .join(reply).fetchJoin().on(comment.id.eq(reply.comment.id))
                .where(
                        listEntity.id.eq(list.getId()),
                        comment.id.gt(cursorId),
                        comment.user.isDelete.eq(false)
                )
                .orderBy(comment.id.asc())
                .limit(size + 1)
                .fetch();
    }
}
