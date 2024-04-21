package com.listywave.alarm.application.domain;

import static com.listywave.alarm.application.domain.AlarmType.COLLABORATOR;
import static com.listywave.alarm.application.domain.AlarmType.COLLECT;
import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.FOLLOW;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;
import static com.listywave.common.exception.ErrorCode.CANNOT_SEND_OWN_ALARM;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.common.exception.CustomException;
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

    public static AlarmEvent follow(User publisher, User listenerUser) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(listenerUser.getId())
                .listId(null)
                .commentId(null)
                .alarmType(FOLLOW)
                .build();
    }

    public static AlarmEvent collect(User publisher, ListEntity list) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(list.getUser().getId())
                .listId(list.getId())
                .commentId(null)
                .alarmType(COLLECT)
                .build();
    }

    public static AlarmEvent comment(ListEntity list, Comment comment) {
        return AlarmEvent.builder()
                .publisher(comment.getUser())
                .listenerId(list.getUser().getId())
                .listId(list.getId())
                .commentId(comment.getId())
                .alarmType(COMMENT)
                .build();
    }

    public static AlarmEvent reply(Comment comment, Reply reply) {
        return AlarmEvent.builder()
                .publisher(reply.getUser())
                .listenerId(comment.getUser().getId())
                .listId(comment.getList().getId())
                .commentId(comment.getId())
                .alarmType(REPLY)
                .build();
    }

    public static AlarmEvent collaborator(Collaborator collaborator, User publisher) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(collaborator.getUser().getId())
                .listId(collaborator.getList().getId())
                .commentId(null)
                .alarmType(COLLABORATOR)
                .build();
    }

    public void validateDifferentPublisherAndReceiver() {
        if (publisher.isSame(listenerId)) {
            throw new CustomException(CANNOT_SEND_OWN_ALARM);
        }
    }
}
