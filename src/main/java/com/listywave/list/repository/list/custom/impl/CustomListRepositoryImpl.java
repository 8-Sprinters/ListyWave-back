package com.listywave.list.repository.list.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QLabel.label;
import static com.listywave.list.application.domain.QLists.lists;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.repository.list.custom.CustomListRepository;
import com.listywave.user.application.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomListRepositoryImpl implements CustomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Lists> findTrandingLists() {
        List<Lists> fetch = queryFactory
                .selectFrom(lists)
                .leftJoin(lists.items, item)
                .distinct()
                .limit(10)
                .orderBy(lists.collectCount.desc(), lists.viewCount.desc())
                .fetch();
        return fetch;
    }

    @Override
    public List<Lists> getRecentLists() {
        List<Lists> fetch = queryFactory
                .selectFrom(lists)
                .join(lists.user, user).fetchJoin()
                .leftJoin(lists.labels, label)
                .leftJoin(lists.items, item)
                .distinct()
                .limit(10)
                .orderBy(lists.updatedDate.desc())
                .fetch();
        return fetch;
    }

    @Override
    public List<Lists> getRecentListsByFollowing(List<User> followingUsers) {
        List<Lists> fetch = queryFactory
                .selectFrom(lists)
                .join(lists.user, user).fetchJoin()
                .leftJoin(lists.labels, label)
                .leftJoin(lists.items, item)
                .where(lists.user.id.in(
                        followingUsers.stream()
                                .map(BaseEntity::getId)
                                .collect(Collectors.toList())
                ))
                .distinct()
                .limit(10)
                .orderBy(lists.updatedDate.desc())
                .fetch();
        return fetch;
    }
}
