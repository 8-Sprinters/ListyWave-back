package com.listywave.alarm.application.domain;

import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;

public record AlarmDeleteEvent(
        Comment comment,
        Reply reply,
        AlarmType type
) {

    public static AlarmDeleteEvent comment(Comment comment) {
        return new AlarmDeleteEvent(comment, null, COMMENT);
    }

    public static AlarmDeleteEvent reply(Reply reply) {
        return new AlarmDeleteEvent(null, reply, REPLY);
    }
}
