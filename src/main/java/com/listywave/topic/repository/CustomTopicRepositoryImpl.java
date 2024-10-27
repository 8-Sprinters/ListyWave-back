package com.listywave.topic.repository;

import static com.listywave.topic.application.domain.QTopic.topic;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.topic.application.domain.Topic;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomTopicRepositoryImpl implements CustomTopicRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Topic> findAllExposed(@Nullable Long cursorId, int size) {
        return queryFactory
                .selectFrom(topic)
                .join(user).on(topic.user.id.eq(user.id))
                .where(
                        cursorIdLowerThan(cursorId),
                        topic.isExposed.isTrue()
                )
                .limit(size + 1)
                .orderBy(topic.id.desc())
                .fetch();
    }

    private BooleanExpression cursorIdLowerThan(Long cursorId) {
        return cursorId == null ? null : topic.id.lt(cursorId);
    }

    @Override
    public List<Topic> findAll(@Nullable Long cursorId, int size) {
        return queryFactory
                .selectFrom(topic)
                .join(user).on(topic.user.id.eq(user.id))
                .where(
                        cursorIdGreaterThan(cursorId)
                )
                .limit(size + 1)
                .orderBy(topic.id.asc())
                .fetch();
    }

    private BooleanExpression cursorIdGreaterThan(Long cursorId) {
        return cursorId == null ? null : topic.id.gt(cursorId);
    }
}
