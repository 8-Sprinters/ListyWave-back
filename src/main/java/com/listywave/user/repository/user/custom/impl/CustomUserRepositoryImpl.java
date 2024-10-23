package com.listywave.user.repository.user.custom.impl;

import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.search.UserSearchResult;
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
    public List<User> getRecommendUsers(List<User> myFollowingUsers, User me) {
        List<Long> myFollowingUserIds = myFollowingUsers.stream()
                .map(User::getId)
                .toList();

        return queryFactory
                .select(user)
                .from(listEntity)
                .rightJoin(listEntity.user, user)
                .where(
                        userIdNotEqual(me),
                        userIdNotIn(myFollowingUserIds),
                        user.isDelete.isFalse()
                )
                .groupBy(user)
                .orderBy(listEntity.updatedDate.max().desc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression userIdNotIn(List<Long> myFollowingUserIds) {
        return myFollowingUserIds.isEmpty() ? null : user.id.notIn(myFollowingUserIds);
    }

    private BooleanExpression userIdNotEqual(User me) {
        return me == null ? null : user.id.ne(me.getId());
    }

    @Override
    public Long countBySearch(String search, Long loginUserId) {
        if (search.isEmpty()) {
            return 0L;
        }
        return queryFactory
                .select(user.count())
                .from(user)
                .where(
                        userIdNe(loginUserId),
                        user.nickname.value.contains(search),
                        user.isDelete.isFalse()
                )
                .fetchOne();
    }

    private BooleanExpression userIdNe(Long loginUserId) {
        return loginUserId == null ? null : user.id.ne(loginUserId);
    }

    @Override
    public Slice<UserSearchResult> findAllBySearch(String search, Pageable pageable, Long loginUserId) {
        if (search.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<UserSearchResult> fetch = queryFactory
                .select(Projections.fields(UserSearchResult.class,
                        user.id,
                        user.nickname.value.as("nickname"),
                        user.profileImageUrl.value.as("profileImageUrl")
                ))
                .from(user)
                .where(
                        userIdNe(loginUserId),
                        user.nickname.value.contains(search),
                        user.isDelete.isFalse()
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
}
