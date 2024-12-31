package com.listywave.alarm.application.domain;

import static com.listywave.alarm.application.domain.AlarmType.COLLECT;
import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.FOLLOW;
import static com.listywave.alarm.application.domain.AlarmType.NOTICE;
import static com.listywave.alarm.application.domain.AlarmType.REACTION;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.mention.Mention;
import com.listywave.notice.application.domain.Notice;
import com.listywave.user.application.domain.User;
import java.util.List;
import lombok.Builder;

@Builder
public record AlarmCreateEvent(
        User publisher,
        Long listenerId,
        ListEntity list,
        Comment comment,
        Reply reply,
        List<Mention> mentions,
        Notice notice,
        AlarmType alarmType
) {

    public Alarm toEntity() {
        return Alarm.builder()
                .sendUser(publisher)
                .receiveUserId(listenerId)
                .list(list)
                .comment(comment)
                .reply(reply)
                .type(alarmType)
                .notice(notice)
                .isChecked(false)
                .build();
    }

    public static AlarmCreateEvent comment(ListEntity list, Comment comment, List<Mention> mentions) {
        return AlarmCreateEvent.builder()
                .publisher(comment.getUser())
                .listenerId(list.getUser().getId())
                .list(list)
                .comment(comment)
                .mentions(mentions)
                .alarmType(COMMENT)
                .build();
    }

    public static AlarmCreateEvent reply(Comment comment, Reply reply, List<Mention> mentions) {
        return AlarmCreateEvent.builder()
                .publisher(reply.getUser())
                .listenerId(comment.getUser().getId())
                .list(comment.getList())
                .comment(comment)
                .reply(reply)
                .mentions(mentions)
                .alarmType(REPLY)
                .build();
    }

    public static AlarmCreateEvent follow(User publisher, User listenerUser) {
        return AlarmCreateEvent.builder()
                .publisher(publisher)
                .listenerId(listenerUser.getId())
                .alarmType(FOLLOW)
                .build();
    }

    public static AlarmCreateEvent collect(User publisher, ListEntity list) {
        return AlarmCreateEvent.builder()
                .publisher(publisher)
                .listenerId(list.getUser().getId())
                .list(list)
                .alarmType(COLLECT)
                .build();
    }

    public static AlarmCreateEvent notice(User user, Notice notice) {
        return AlarmCreateEvent.builder()
                .publisher(user)
                .notice(notice)
                .alarmType(NOTICE)
                .build();
    }

    public static AlarmCreateEvent reaction(User publisher, ListEntity list) {
        return AlarmCreateEvent.builder()
                .publisher(publisher)
                .listenerId(list.getUser().getId())
                .list(list)
                .alarmType(REACTION)
                .build();
    }

    public boolean isToMyself() {
        return this.publisher.isSame(listenerId);
    }
}
