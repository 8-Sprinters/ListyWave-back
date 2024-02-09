package com.listywave.user.repository.user.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QLists.lists;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.CollaboratorResponse;
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
    public List<Lists> findFeedLists(Long userId, String type, CategoryType category, Long cursorId, int size) {
        return queryFactory
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
        return queryFactory
                .select(user)
                .from(lists)
                .rightJoin(lists.user, user)
                .groupBy(user)
                .orderBy(lists.updatedDate.max().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Long getCollaboratorCount(String search, User me) {
        if (search.isEmpty()) {
            return 0L;
        }
        return queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.id.ne(me.getId()),
                        user.nickname.value.contains(search)
                )
                .fetchOne();
    }

    @Override
    public Slice<CollaboratorResponse> getCollaborators(String search, Pageable pageable, User me) {
        if (search.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<CollaboratorResponse> collaborators = queryFactory
                .select(Projections.fields(CollaboratorResponse.class,
                        user.id,
                        user.nickname.value.as("nickname"),
                        user.profileImageUrl.value.as("profileImageUrl")
                ))
                .from(user)
                .where(
                        user.id.ne(me.getId()),
                        user.nickname.value.contains(search)
                )
                .orderBy(
                        Expressions.stringTemplate("LOCATE({0}, {1})", search, user.nickname.value).asc(),
                        user.nickname.value.asc()
                )
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch();
        return checkEndPage(pageable, collaborators);
    }

    private Slice<CollaboratorResponse> checkEndPage(Pageable pageable, List<CollaboratorResponse> results) {
        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}
