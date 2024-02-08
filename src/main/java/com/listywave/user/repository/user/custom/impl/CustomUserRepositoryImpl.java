package com.listywave.user.repository.user.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QLists.lists;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.custom.CustomUserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Lists> findFeedLists(Long userId, String type, CategoryType category, Long cursorId, int size) {
        List<Lists> fetch = queryFactory
                .select(lists)
                .from(lists)
                .leftJoin(lists.items, item)
                .where(
                        userIdEq(userId),
                        typeEq(type),
                        categoryEq(category),
                        listIdLt(cursorId)
                )
                .distinct()
                .limit(size + 1)
                .orderBy(lists.updatedDate.desc())
                .fetch();
        return fetch;
    }

    private BooleanExpression listIdLt(Long cursorId) {
        return cursorId == null ? null : lists.id.lt(cursorId);
    }

    private BooleanExpression categoryEq(CategoryType categoryCode) {
        if ("0".equals(categoryCode.getCodeValue())) {
            return null;
        }
        return lists.category.eq(categoryCode);
    }

    private BooleanExpression typeEq(String type) {
        if (type.equals("my")) {
            return lists.hasCollaboration.eq(false);
        }
        return lists.hasCollaboration.eq(true);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : lists.user.id.eq(userId);
    }

    @Override
    public List<User> getRecommendUsers() {
        List<User> fetch = queryFactory
                .select(user)
                .from(lists)
                .rightJoin(lists.user, user)
                .groupBy(user)
                .orderBy(lists.updatedDate.max().desc())
                .limit(10)
                .fetch();
        return fetch;
    }
}
