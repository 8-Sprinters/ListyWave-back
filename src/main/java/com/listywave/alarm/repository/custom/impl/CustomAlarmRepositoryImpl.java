package com.listywave.alarm.repository.custom.impl;

import static com.listywave.alarm.application.domain.QAlarm.alarm;

import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAlarmRepositoryImpl implements CustomAlarmRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteAlarmThirtyDaysAgo() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        queryFactory.delete(alarm)
                .where(alarm.createdDate.lt(thirtyDaysAgo))
                .execute();
    }
}
