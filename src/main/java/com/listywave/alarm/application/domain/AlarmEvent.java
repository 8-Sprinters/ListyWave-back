package com.listywave.alarm.application.domain;

import static com.listywave.alarm.application.domain.AlarmType.COLLECT;
import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.FOLLOW;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import java.util.List;
import lombok.Builder;

@Builder
public record AlarmEvent(
        User publisher,
        Long listenerId,
        ListEntity list,
        Comment comment,
        Reply reply,
        List<Mention> mentions,
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
                .isChecked(false)
                .build();
    }

    public static AlarmEvent comment(ListEntity list, Comment comment, List<Mention> mentions) {
        return AlarmEvent.builder()
                .publisher(comment.getUser())
                .listenerId(list.getUser().getId())
                .list(list)
                .comment(comment)
                .mentions(mentions)
                .alarmType(COMMENT)
                .build();
    }

    public static AlarmEvent reply(Comment comment, Reply reply, List<Mention> mentions) {
        return AlarmEvent.builder()
                .publisher(reply.getUser())
                .listenerId(comment.getUser().getId())
                .list(comment.getList())
                .comment(comment)
                .reply(reply)
                .mentions(mentions)
                .alarmType(REPLY)
                .build();
    }

    public static AlarmEvent follow(User publisher, User listenerUser) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(listenerUser.getId())
                .alarmType(FOLLOW)
                .build();
    }

    public static AlarmEvent collect(User publisher, ListEntity list) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(list.getUser().getId())
                .list(list)
                .alarmType(COLLECT)
                .build();
    }

    /*public static AlarmEvent collaborator(Collaborator collaborator, User publisher) {
        return AlarmEvent.builder()
                .publisher(publisher)
                .listenerId(collaborator.getUser().getId())
                .listId(collaborator.getList().getId())
                .commentId(null)
                .alarmType(COLLABORATOR)
                .build();
    }*/

    public boolean isToMyself() {
        return this.publisher.isSame(listenerId);
    }
}
