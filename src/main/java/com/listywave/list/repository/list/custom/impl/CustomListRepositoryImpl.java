package com.listywave.list.repository.list.custom.impl;

import static com.listywave.collaborator.application.domain.QCollaborator.collaborator;
import static com.listywave.collection.application.domain.QCollect.collect;
import static com.listywave.common.exception.ErrorCode.NOT_SUPPORT_FILTER_ARGUMENT_EXCEPTION;
import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.label.QLabel.label;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.custom.CustomListRepository;
import com.listywave.user.application.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomListRepositoryImpl implements CustomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ListEntity> findTrandingLists() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        listEntity.isPublic.eq(true),
                        listEntity.user.isDelete.eq(false)
                )
                .distinct()
                .limit(10)
                .orderBy(listEntity.collectCount.desc(), listEntity.viewCount.desc(), listEntity.id.desc())
                .fetch();
    }

    @Override
    public Slice<ListEntity> getRecentLists(LocalDateTime cursorUpdatedDate, Pageable pageable) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<ListEntity> fetch = queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(label).on(listEntity.id.eq(label.list.id))
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        updatedDateLt(cursorUpdatedDate),
                        listEntity.user.isDelete.eq(false),
                        listEntity.isPublic.eq(true)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();

        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression updatedDateLt(LocalDateTime cursorUpdatedDate) {
        if (cursorUpdatedDate == null) {
            return null;
        }
        return listEntity.updatedDate.lt(cursorUpdatedDate);
    }

    @Override
    public Slice<ListEntity> getRecentListsByFollowing(
            List<User> followingUsers,
            LocalDateTime cursorUpdatedDate,
            Pageable pageable
    ) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Long> followingUserIds = followingUsers.stream()
                .map(User::getId)
                .toList();

        List<ListEntity> fetch = queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(label).on(listEntity.id.eq(label.list.id))
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        listEntity.user.id.in(followingUserIds),
                        updatedDateLt(cursorUpdatedDate),
                        listEntity.isPublic.eq(true)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();

        return checkEndPage(pageable, fetch);
    }

    @Override
    public Slice<ListEntity> findFeedLists(
            Long userId,
            String type,
            CategoryType category,
            LocalDateTime cursorUpdatedDate,
            Pageable pageable
    ) {
        List<ListEntity> fetch = queryFactory
                .selectDistinct(listEntity)
                .from(listEntity)
                .leftJoin(collaborator).on(collaborator.list.id.eq(listEntity.id))
                .where(
                        typeEq(type, userId),
                        categoryEq(category),
                        updatedDateLt(cursorUpdatedDate),
                        listEntity.user.isDelete.isFalse()
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression typeEq(String type, Long userId) {
        if (type.equals("my")) {
            return listEntity.user.id.eq(userId).and(listEntity.hasCollaboration.isFalse());
        }
        if (type.equals("collabo")) {
            return collaborator.user.id.eq(userId)
                    .or(listEntity.hasCollaboration.isTrue().and(listEntity.user.id.eq(userId)));
        }
        throw new CustomException(NOT_SUPPORT_FILTER_ARGUMENT_EXCEPTION);
    }

    private BooleanExpression categoryEq(CategoryType category) {
        if (category.equals(ENTIRE)) {
            return null;
        }
        return listEntity.category.eq(category);
    }

    @Override
    public List<ListEntity> findAllCollectedListBy(Long userId) {
        return queryFactory.selectFrom(listEntity)
                .leftJoin(collect).on(collect.list.id.eq(listEntity.id))
                .where(
                        collect.userId.eq(userId)
                )
                .fetch();
    }
}
