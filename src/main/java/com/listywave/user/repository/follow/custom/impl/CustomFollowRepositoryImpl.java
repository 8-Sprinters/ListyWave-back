package com.listywave.user.repository.follow.custom.impl;

import static com.listywave.user.application.domain.QFollow.follow;

import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.custom.CustomFollowRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFollowRepositoryImpl implements CustomFollowRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Follow> findAllByFollowingUserOrderByFollowerUserNicknameDesc(User followingUser, int size, int cursorId) {
        return queryFactory.selectFrom(follow)
                .where(
                        follow.followingUser.id.eq(followingUser.getId()),
                        follow.followingUser.id.gt(cursorId)
                )
                .orderBy(follow.followerUser.nickname.value.asc())
                .limit(size + 1)
                .fetch();
    }
}
