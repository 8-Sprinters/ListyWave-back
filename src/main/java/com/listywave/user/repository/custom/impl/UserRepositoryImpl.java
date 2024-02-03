package com.listywave.user.repository.custom.impl;

import static com.listywave.list.application.domain.QItem.*;
import static com.listywave.list.application.domain.QLists.*;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.repository.custom.UserRepositoryCustom;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
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
                        listIdGt(cursorId)
                )
                .distinct()
                .limit(size + 1)
                .orderBy(lists.updatedDate.desc())
                .fetch();
        return fetch;
    }

    private BooleanExpression listIdGt(Long cursorId) {
        return cursorId == null ? null : lists.id.lt(cursorId);
    }

    private BooleanExpression categoryEq(CategoryType categoryCode) {

        return "0".equals(categoryCode.getCodeValue()) ? null : lists.category.eq(categoryCode);
    }

    private BooleanExpression typeEq(String type) {
        if(type.equals("my")){
            return lists.hasCollaboration.eq(false);
        }
        return lists.hasCollaboration.eq(true);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : lists.user.id.eq(userId);
    }
}
