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
            String cursorNickname
    ) {
        List<User> fetch = queryFactory
                .selectFrom(user)
                .join(follow).on(follow.followerUser.id.eq(user.id))
                .where(
                        followingUserNicknameGt(cursorNickname),
                        follow.followingUser.id.eq(followingUser.getId()),
                        follow.followerUser.nickname.value.contains(search),
                        user.isDelete.isFalse()
                )
                .orderBy(follow.followerUser.nickname.value.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return checkEndPage(pageable, fetch);
    }

    private BooleanExpression followingUserNicknameGt(String cursorNickname) {
        return cursorNickname == null ? null : follow.followerUser.nickname.value.gt(cursorNickname);
    }

    @Override
    public Long countFollowerUserBy(User followingUser, String search, String cursorNickname) {
        return queryFactory
                .select(user.count())
                .from(user)
                .join(follow).on(follow.followerUser.id.eq(user.id))
                .where(
                        followingUserNicknameGt(cursorNickname),
                        follow.followingUser.id.eq(followingUser.getId()),
                        follow.followerUser.nickname.value.contains(search),
                        user.isDelete.isFalse()
                )
                .fetchOne();
    }

    @Override
    public List<User> findAllFollowingUserBy(User followerUser, String search) {
        return queryFactory.selectFrom(user)
                .join(follow).on(follow.followingUser.id.eq(user.id))
                .where(
                        follow.followerUser.id.eq(followerUser.getId()),
                        follow.followingUser.nickname.value.contains(search),
                        user.isDelete.eq(false)
                )
                .orderBy(follow.followingUser.nickname.value.asc())
                .fetch();
    }
}
