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
    public List<Follow> findAllByFollowingUser(User followingUser, int size, int cursorId) {
        return queryFactory.selectFrom(follow)
                .where(
                        follow.followingUser.eq(followingUser),
                        follow.followingUser.id.gt(cursorId)
                )
                .orderBy(follow.createdDate.desc())
                .limit(size + 1)
                .fetch();
    }
}
