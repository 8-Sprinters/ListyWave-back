package com.listywave.user.repository.follow.custom.impl;

import static com.listywave.common.util.PaginationUtils.checkEndPage;
import static com.listywave.user.application.domain.QFollow.follow;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.custom.CustomFollowRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@RequiredArgsConstructor
public class CustomFollowRepositoryImpl implements CustomFollowRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<User> findAllFollowerUserBy(
            User followingUser,
            Pageable pageable,
            String search,
            String cursorId
    ) {
        List<User> fetch = queryFactory.selectFrom(user)
                .join(follow).on(follow.followerUser.id.eq(user.id))
                .where(
                        followingUserNicknameGt(cursorId),
                        follow.followingUser.id.eq(followingUser.getId()),
                        follow.followerUser.nickname.value.contains(search)
                )
                .orderBy(follow.followerUser.nickname.value.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression followingUserNicknameGt(String cursorId) {
        return cursorId == null ? null : follow.followerUser.nickname.value.gt(cursorId);
    }

    @Override
    public List<User> findAllFollowingUserBy(User followerUser, String search) {
        return queryFactory.selectFrom(user)
                .join(follow).on(follow.followingUser.id.eq(user.id))
                .where(
                        follow.followerUser.id.eq(followerUser.getId()),
                        follow.followingUser.nickname.value.contains(search)
                )
                .orderBy(follow.followingUser.nickname.value.asc())
                .fetch();
    }
}
