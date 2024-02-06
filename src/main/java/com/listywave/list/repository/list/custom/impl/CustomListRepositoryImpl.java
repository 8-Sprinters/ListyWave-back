package com.listywave.list.repository.list.custom.impl;

import static com.listywave.list.application.domain.QItem.item;
import static com.listywave.list.application.domain.QLists.lists;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.repository.list.custom.CustomListRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomListRepositoryImpl implements CustomListRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Lists> findTrandingLists() {
        List<Lists> fetch = queryFactory
                .selectFrom(lists)
                .from(lists)
                .leftJoin(lists.items, item)
                .distinct()
                .limit(10)
                .orderBy(lists.viewCount.desc(), lists.collectCount.desc())
                .fetch();

        fetch.forEach(
                list -> list.getItems()
                        .sort(Comparator.comparing(Item::getRanking))
        );
        return fetch;
    }
}
