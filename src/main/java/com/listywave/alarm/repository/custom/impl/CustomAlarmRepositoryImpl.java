package com.listywave.alarm.repository.custom.impl;

import static com.listywave.alarm.application.domain.QAlarm.alarm;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;

import com.listywave.alarm.application.dto.FindAlarmResponse;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import com.listywave.user.application.domain.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAlarmRepositoryImpl implements CustomAlarmRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindAlarmResponse> getAlarms(User user) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return queryFactory
                .select(Projections.fields(FindAlarmResponse.class,
                        alarm.id,
                        alarm.user.id.as("sendUserId"),
                        alarm.user.nickname.value.as("nickname"),
                        alarm.user.profileImageUrl.value.as("profileImageUrl"),
                        alarm.listId,
                        alarm.commentId,
                        listEntity.title.value.as("listTitle"),
                        alarm.type.stringValue().as("type"),
                        alarm.isChecked,
                        alarm.createdDate
                ))
                .from(alarm)
                .leftJoin(listEntity).on(alarm.listId.eq(listEntity.id))
                .where(
                        alarm.receiveUserId.eq(user.getId()),
                        alarm.createdDate.goe(thirtyDaysAgo)
                )
                .orderBy(alarm.id.desc())
                .fetch();
    }

    @Override
    public void deleteAlarmThirtyDaysAgo() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        queryFactory.delete(alarm)
                .where(alarm.createdDate.lt(thirtyDaysAgo))
                .execute();
    }
}
