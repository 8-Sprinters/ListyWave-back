package com.listywave.collection.repository.custom.impl;

import static com.listywave.collection.application.domain.QCollect.collect;
import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.repository.custom.CustomCollectionRepository;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.user.application.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;

@RequiredArgsConstructor
public class CustomCollectionRepositoryImpl implements CustomCollectionRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Collect> getAllCollectionList(Long cursorId, Pageable pageable, Long userId, Long folderId) {
        List<Collect> fetch = queryFactory
                .selectFrom(collect)
                .join(collect.list, listEntity).fetchJoin()
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        collectIdLt(cursorId),
                        userIdEq(userId),
                        folderIdEq(folderId)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(collect.id.desc())
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression folderIdEq(Long folderId) {
        return collect.folder.id.eq(folderId);
    }

    private BooleanExpression collectIdLt(Long cursorId) {
        return cursorId == null ? null : collect.id.lt(cursorId);
    }

    @Override
    public List<CategoryType> getCategoriesByCollect(User user) {
        return queryFactory.select(listEntity.category)
                .from(collect)
                .join(listEntity).on(listEntity.id.eq(collect.list.id))
                .where(userIdEq(user.getId()))
                .groupBy(listEntity.category)
                .fetch();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId == null ? null : collect.userId.eq(userId);
    }
}
