package com.listywave.collection.repository.custom.impl;

import static com.listywave.collection.application.domain.QCollect.collect;
import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.repository.custom.CustomCollectionRepository;
import com.listywave.list.application.domain.category.CategoryType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomCollectionRepositoryImpl implements CustomCollectionRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Collect> getAllCollectionList(Long cursorId, Pageable pageable, Long userId, CategoryType category) {
        List<Collect> fetch = queryFactory
                .selectFrom(collect)
                .join(collect.list, listEntity).fetchJoin()
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        collectIdLt(cursorId),
                        userIdEq(userId),
                        categoryEq(category)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(collect.id.desc())
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : collect.userId.eq(userId);
    }

    private BooleanExpression collectIdLt(Long cursorId) {
        return cursorId == null ? null : collect.id.lt(cursorId);
    }

    private BooleanExpression categoryEq(CategoryType category) {
        if ("0".equals(category.getCodeValue())) {
            return null;
        }
        return listEntity.category.eq(category);
    }
}
