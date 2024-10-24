package com.listywave.alarm.repository.custom.impl;

import static com.listywave.alarm.application.domain.QAlarm.alarm;
import static com.listywave.list.application.domain.comment.QComment.comment;
import static com.listywave.list.application.domain.list.QListEntity.listEntity;
import static com.listywave.list.application.domain.reply.QReply.reply;

import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import com.listywave.user.application.domain.QUser;
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
    public List<AlarmFindResponse> findAllBy(User user) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return queryFactory
                .select(Projections.constructor(AlarmFindResponse.class,
                        alarm.id,
                        alarm.createdDate,
                        alarm.isChecked,
                        alarm.type.stringValue(),
                        Projections.constructor(AlarmFindResponse.UserDto.class,
                                alarm.sendUser.id,
                                alarm.sendUser.nickname.value,
                                alarm.sendUser.profileImageUrl.value
                        ),
                        Projections.constructor(AlarmFindResponse.ListDto.class,
                                alarm.list.id,
                                alarm.list.title.value
                        ),
                        Projections.constructor(AlarmFindResponse.CommentDto.class,
                                alarm.comment.id,
                                alarm.comment.commentContent.value,
                                null
                        ),
                        Projections.constructor(AlarmFindResponse.ReplyDto.class,
                                alarm.reply.id,
                                alarm.reply.commentContent.value,
                                null
                        )
                ))
                .from(alarm)
                .join(QUser.user).on(alarm.sendUser.id.eq(QUser.user.id))
                .join(listEntity).on(alarm.list.id.eq(listEntity.id))
                .join(comment).on(alarm.comment.id.eq(comment.id))
                .join(reply).on(alarm.reply.id.eq(reply.id))
                .where(
                        alarm.receiveUserId.eq(user.getId()),
                        alarm.createdDate.goe(thirtyDaysAgo)
                )
                .orderBy(alarm.id.desc())
                .fetch();

//        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
//
//        return queryFactory
//                .select(Projections.fields(AlarmFindResponse.class,
//                        alarm.id,
//                        alarm.user.id.as("sendUserId"),
//                        alarm.user.nickname.value.as("nickname"),
//                        alarm.user.profileImageUrl.value.as("profileImageUrl"),
//                        alarm.listId,
//                        alarm.commentId,
//                        listEntity.title.value.as("listTitle"),
//                        alarm.type.stringValue().as("type"),
//                        alarm.isChecked,
//                        alarm.createdDate
//                ))
//                .from(alarm)
//                .leftJoin(listEntity).on(alarm.listId.eq(listEntity.id))
//                .where(
//                        alarm.receiveUserId.eq(user.getId()),
//                        alarm.createdDate.goe(thirtyDaysAgo)
//                )
//                .orderBy(alarm.id.desc())
//                .fetch();
    }

    @Override
    public void deleteAlarmThirtyDaysAgo() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        queryFactory.delete(alarm)
                .where(alarm.createdDate.lt(thirtyDaysAgo))
                .execute();
    }
}
