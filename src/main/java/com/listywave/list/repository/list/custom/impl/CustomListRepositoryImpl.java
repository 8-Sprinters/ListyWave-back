package com.listywave.list.repository.list.custom.impl;

import static com.listywave.collaborator.application.domain.QCollaborator.collaborator;
import static com.listywave.collection.application.domain.QCollect.collect;
import static com.listywave.common.exception.ErrorCode.NOT_SUPPORT_FILTER_ARGUMENT_EXCEPTION;
import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.comment.QComment.comment;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.label.QLabel.label;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.list.application.domain.reply.QReply.reply;
import static com.listywave.user.application.domain.QUser.user;
import static com.querydsl.jpa.JPAExpressions.select;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.repository.list.custom.CustomListRepository;
import com.listywave.user.application.domain.User;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomListRepositoryImpl implements CustomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ListTrandingResponse> fetchTrandingLists() {
        List<ListTrandingResponse> responses = getTrandingResponses();
        return responses.stream()
                .map(t -> t.with(getRepresentImageUrl(t.id())))
                .collect(Collectors.toList());
    }

    private List<ListTrandingResponse> getTrandingResponses() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        NumberPath<Long> trandingScoreAlias = Expressions.numberPath(Long.class, "trandingScore");

        return queryFactory
                .select(Projections.constructor(ListTrandingResponse.class,
                        listEntity.id.as("id"),
                        listEntity.user.id.as("ownerId"),
                        listEntity.user.nickname.value.as("ownerNickname"),
                        listEntity.user.profileImageUrl.value.as("ownerProfileImageUrl"),
                        listEntity.title.value.as("title"),
                        listEntity.description.value.as("description"),
                        listEntity.backgroundColor.as("backgroundColor"),
                        ExpressionUtils.as(
                                select(
                                        listEntity.collectCount.multiply(3).add(
                                                comment.countDistinct().add(reply.count()).multiply(2)
                                        ).castToNum(Long.class)
                                )
                                        .from(comment)
                                        .leftJoin(reply).on(reply.comment.id.eq(comment.id))
                                        .where(comment.list.id.eq(listEntity.id)), trandingScoreAlias))
                )
                .from(listEntity)
                .join(listEntity.user, user)
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        listEntity.isPublic.eq(true),
                        listEntity.user.isDelete.eq(false)
                )
                .distinct()
                .orderBy(trandingScoreAlias.desc())
                .limit(10)
                .fetch();
    }

    private String getRepresentImageUrl(Long id) {
        String imageUrl = queryFactory
                .select(item.imageUrl.value)
                .from(item)
                .where(
                        item.list.id.eq(id).and(
                                item.imageUrl.value.ne("")
                        )
                )
                .orderBy(item.ranking.asc())
                .limit(1)
                .fetchOne();

        return imageUrl != null ? imageUrl : "";
    }

    @Override
    public Slice<ListEntity> getRecentLists(LocalDateTime cursorUpdatedDate, CategoryType category, Pageable pageable) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<ListEntity> fetch = queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(item).on(listEntity.id.eq(item.list.id))
                .where(
                        listEntity.updatedDate.goe(thirtyDaysAgo),
                        updatedDateLt(cursorUpdatedDate),
                        categoryEq(category),
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
