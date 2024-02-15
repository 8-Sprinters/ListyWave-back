package com.listywave.list.repository.list.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QLabel.label;
import static com.listywave.list.application.domain.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.list.application.domain.list.ListEntity;
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
    public List<ListEntity> findTrandingLists() {
        return queryFactory
                .selectFrom(listEntity)
                .leftJoin(listEntity.items, item)
                .distinct()
                .limit(10)
                .orderBy(listEntity.collectCount.desc(), listEntity.viewCount.desc())
                .fetch();
    }

    @Override
    public List<ListEntity> getRecentLists() {
        return queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(listEntity.labels, label)
                .leftJoin(listEntity.items, item)
                .distinct()
                .limit(10)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();
    }

    @Override
    public List<ListEntity> getRecentListsByFollowing(List<User> followingUsers) {
        return queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(listEntity.labels, label)
                .leftJoin(listEntity.items, item)
                .where(listEntity.user.id.in(
                        followingUsers.stream()
                                .map(User::getId)
                                .collect(Collectors.toList())
                ))
                .distinct()
                .limit(10)
                .orderBy(listEntity.updatedDate.desc())
                .fetch();
    }
}
