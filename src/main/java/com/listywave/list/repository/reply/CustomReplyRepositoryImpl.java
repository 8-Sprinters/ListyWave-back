package com.listywave.list.repository.reply;

import static com.listywave.list.application.domain.QComment.comment;
import static com.listywave.list.application.domain.QListEntity.listEntity;
import static com.listywave.list.application.domain.QReply.reply;

import com.listywave.list.application.domain.ListEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomReplyRepositoryImpl implements CustomReplyRepository {

    private final JPAQueryFactory queryFactory;

    public Long countByList(ListEntity list) {
        return queryFactory.select(reply.count())
                .from(reply)
                .leftJoin(comment).on(comment.id.eq(reply.id))
                .leftJoin(listEntity).on(listEntity.id.eq(comment.list.id))
                .where(listEntity.id.eq(list.getId()))
                .fetchOne();
    }
}
