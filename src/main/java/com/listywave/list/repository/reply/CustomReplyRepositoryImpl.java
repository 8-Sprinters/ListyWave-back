package com.listywave.list.repository.reply;

import static com.listywave.list.application.domain.QComment.comment;
import static com.listywave.list.application.domain.QLists.lists;
import static com.listywave.list.application.domain.QReply.reply;

import com.listywave.list.application.domain.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomReplyRepositoryImpl implements CustomReplyRepository {

    private final JPAQueryFactory queryFactory;

    public Long countByList(Lists list) {
        return queryFactory.select(reply.count())
                .from(reply)
                .leftJoin(comment).on(comment.id.eq(reply.id))
                .leftJoin(lists).on(lists.id.eq(comment.list.id))
                .fetchOne();
    }
}
