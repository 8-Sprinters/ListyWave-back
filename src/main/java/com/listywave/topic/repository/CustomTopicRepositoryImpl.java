package com.listywave.topic.repository;

import static com.listywave.topic.application.domain.QTopic.topic;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.topic.application.domain.Topic;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomTopicRepositoryImpl implements CustomTopicRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Topic> findAllExposed(Long cursorId, int size) {
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

    private static BooleanExpression cursorIdLowerThan(Long cursorId) {
        return cursorId == null ? null : topic.id.lt(cursorId);
    }
}
