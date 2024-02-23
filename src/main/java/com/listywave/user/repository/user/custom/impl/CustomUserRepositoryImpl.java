package com.listywave.user.repository.user.custom.impl;

import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.search.UserSearchResponse;
import com.listywave.user.repository.user.custom.CustomUserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ListEntity> findFeedLists(
            List<Collaborator> collaborators, Long userId, String type,
            CategoryType category, Long cursorId, Pageable pageable
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
            return listEntity.id.in(
                    collaborators.stream()
                            .map(collaborator -> collaborator.getList().getId())
                            .toList()
            );
        }
        return null;
    }

    private BooleanExpression listIdLt(Long cursorId) {
        return cursorId == null ? null : listEntity.id.lt(cursorId);
    }

    private BooleanExpression categoryEq(CategoryType category) {
        if (category.equals(ENTIRE)) {
            return null;
        }
        return listEntity.category.eq(category);
    }

    private BooleanExpression typeEq(String type) {
        if (type.equals("my")) {
            return listEntity.hasCollaboration.eq(false);
        }
        return listEntity.hasCollaboration.eq(true);
    }

    private BooleanExpression userIdEq(Long userId, String type) {
        if (type.equals("my")) {
            return userId == null ? null : listEntity.user.id.eq(userId);
        }
        return null;
    }

    @Override
    public List<User> getRecommendUsers(List<User> myFollowingUsers, User me) {
        List<Long> myFollowingUserIds = myFollowingUsers.stream()
                .map(User::getId)
                .toList();

        return queryFactory
                .select(user)
                .from(listEntity)
                .rightJoin(listEntity.user, user)
                .where(
                        user.id.ne(me.getId()),
                        user.id.notIn(myFollowingUserIds)
                )
                .groupBy(user)
                .orderBy(listEntity.updatedDate.max().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Long getCollaboratorCount(String search, Long loginUserId) {
        if (search.isEmpty()) {
            return 0L;
        }
        return queryFactory
                .select(user.count())
                .from(user)
                .where(
                        userIdNe(loginUserId),
                        user.nickname.value.contains(search)
                )
                .fetchOne();
    }

    @Override
    public Slice<UserSearchResponse> getCollaborators(String search, Pageable pageable, Long loginUserId) {
        if (search.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<UserSearchResponse> fetch = queryFactory
                .select(Projections.fields(UserSearchResponse.class,
                        user.id,
                        user.nickname.value.as("nickname"),
                        user.profileImageUrl.value.as("profileImageUrl")
                ))
                .from(user)
                .where(
                        userIdNe(loginUserId),
                        user.nickname.value.contains(search)
                )
                .orderBy(
                        Expressions.stringTemplate("LOCATE({0}, {1})", search, user.nickname.value).asc(),
                        user.nickname.value.asc()
                )
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression userIdNe(Long loginUserId) {
        return loginUserId == null ? null : user.id.ne(loginUserId);
    }
}
