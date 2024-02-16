package com.listywave.list.repository.list.custom.impl;

import static com.listywave.list.application.domain.item.QItem.item;
import static com.listywave.list.application.domain.item.QItems.items;
import static com.listywave.list.application.domain.label.QLabel.label;
import static com.listywave.list.application.domain.label.QLabels.labels;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.user.application.domain.QUser.user;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.custom.CustomListRepository;
import com.listywave.user.application.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomListRepositoryImpl implements CustomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ListEntity> findTrandingLists() {
        return queryFactory
                .selectFrom(listEntity)
                .limit(10)
                .orderBy(listEntity.collectCount.desc(), listEntity.viewCount.desc(), listEntity.id.desc())
                .fetch();
    }

    @Override
    public List<ListEntity> getRecentLists() {
        return queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(label).on(listEntity.labels.eq(labels))
                .leftJoin(item).on(listEntity.items.eq(items))
                .distinct()
                .limit(10)
                .orderBy(listEntity.id.desc())
                .fetch();
    }

    @Override
    public List<ListEntity> getRecentListsByFollowing(List<User> followingUsers) {
        List<Long> followingUserIds = followingUsers.stream()
                .map(User::getId)
                .toList();

        return queryFactory
                .selectFrom(listEntity)
                .join(listEntity.user, user).fetchJoin()
                .leftJoin(label).on(listEntity.labels.eq(labels))
                .leftJoin(item).on(listEntity.items.eq(items))
                .where(listEntity.user.id.in(followingUserIds))
                .distinct()
                .limit(10)
                .orderBy(listEntity.id.desc())
                .fetch();
    }
}
