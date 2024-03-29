package com.listywave.list.repository.reply;

import static com.listywave.list.application.domain.comment.QComment.comment;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.list.application.domain.reply.QReply.reply;

import com.listywave.list.application.domain.list.ListEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomReplyRepositoryImpl implements CustomReplyRepository {

    private final JPAQueryFactory queryFactory;

    public Long countByList(ListEntity list) {
        return queryFactory
                .select(reply.count())
                .from(reply)
                .leftJoin(comment).on(comment.id.eq(reply.comment.id))
                .leftJoin(listEntity).on(listEntity.id.eq(comment.list.id))
                .where(
                        listEntity.id.eq(list.getId()),
                        reply.user.isDelete.isFalse()
                )
                .fetchOne();
    }
}
