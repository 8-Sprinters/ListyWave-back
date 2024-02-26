package com.listywave.list.repository.list.custom.impl;

import static com.listywave.common.exception.ErrorCode.NOT_SUPPORT_FILTER_ARGUMENT_EXCEPTION;
import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.label.QLabel.label;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.collaborator.application.domain.Collaborator;
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
    public Slice<ListEntity> getRecentLists(Long cursorId, Pageable pageable) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<ListEntity> fetch = queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(label).on(listEntity.id.eq(label.list.id))
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        listIdLt(cursorId),
                        listEntity.user.isDelete.eq(false),
                        listEntity.isPublic.eq(true)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();

        return checkEndPage(pageable, fetch);
    }

    @Override
    public Slice<ListEntity> getRecentListsByFollowing(List<User> followingUsers, Long cursorId, Pageable pageable) {
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
                        listIdLt(cursorId),
                        listEntity.isPublic.eq(true)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();

        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression listIdLt(Long cursorId) {
        return cursorId == null ? null : listEntity.id.lt(cursorId);
    }

    @Override
    public Slice<ListEntity> findFeedLists(
            List<Collaborator> collaborators,
            Long userId,
            String type,
            CategoryType category,
            Long cursorId,
            Pageable pageable
    ) {
        List<ListEntity> fetch = queryFactory
                .selectFrom(listEntity)
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        collaboEq(collaborators, type),
                        userIdEq(userId, type),
                        typeEq(type),
                        categoryEq(category),
                        listIdLt(cursorId)
                )
                .distinct()
                .limit(pageable.getPageSize() + 1)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression collaboEq(List<Collaborator> collaborators, String type) {
        if (type.equals("collabo") && collaborators != null) {
            List<Long> collaboratorIds = collaborators.stream()
                    .map(collaborator -> collaborator.getList().getId())
                    .toList();
            return listEntity.id.in(collaboratorIds);
        }
        return null;
    }

    private BooleanExpression userIdEq(Long userId, String type) {
        if (type.equals("my")) {
            return userId == null ? null : listEntity.user.id.eq(userId);
        }
        return null;
    }

    private BooleanExpression typeEq(String type) {
        if (type.equals("my")) {
            return listEntity.hasCollaboration.eq(false);
        }
        if (type.equals("collabo")) {
            return listEntity.hasCollaboration.eq(true);
        }
        throw new CustomException(NOT_SUPPORT_FILTER_ARGUMENT_EXCEPTION);
    }

    private BooleanExpression categoryEq(CategoryType category) {
        if (category.equals(ENTIRE)) {
            return null;
        }
        return listEntity.category.eq(category);
    }
}
