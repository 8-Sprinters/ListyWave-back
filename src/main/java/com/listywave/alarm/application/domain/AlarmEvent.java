package com.listywave.alarm.application.domain;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.user.application.domain.User;
import lombok.Builder;

@Builder
public record AlarmEvent(
        User publisher,
        Long listenerId,
        Long listId,
        Long commentId,
        AlarmType alarmType
) {

    public Alarm toEntity() {
        return Alarm.builder()
                .user(publisher)
                .receiveUserId(listenerId)
                .listId(listId)
                .commentId(commentId)
                .type(alarmType)
                .build();
    }

    public static AlarmEvent followOf(User publisher, User listenerUser) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(listenerUser.getId())
                .listId(null)
                .commentId(null)
                .alarmType(AlarmType.FOLLOW)
                .build();
    }

    public static AlarmEvent collectOf(User publisher, ListEntity list) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(list.getUser().getId())
                .listId(list.getId())
                .commentId(null)
                .alarmType(AlarmType.COLLECT)
                .build();
    }

    public static AlarmEvent commentOf(ListEntity list, Comment comment) {
        return AlarmEvent.builder()
                .publisher(comment.getUser())
                .listenerId(list.getUser().getId())
                .listId(list.getId())
                .commentId(comment.getId())
                .alarmType(AlarmType.COMMENT)
                .build();
    }

    public static AlarmEvent replyOf(Comment comment, Reply reply) {
        return AlarmEvent.builder()
                .publisher(reply.getUser())
                .listenerId(comment.getUser().getId())
                .listId(comment.getList().getId())
                .commentId(comment.getId())
                .alarmType(AlarmType.REPLY)
                .build();
    }

    public boolean isDifferentPublisher() {
        return !publisher.isSame(listenerId);
    }
}
