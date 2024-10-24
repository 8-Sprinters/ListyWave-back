package com.listywave.alarm.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.listywave.alarm.application.domain.Alarm;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record AlarmFindResponse(
        Long id,
        LocalDateTime createdDate,
        @JsonProperty("checked") boolean isChecked,
        String type,
        UserDto sendUser,
        ListDto list,
        CommentDto comment,
        ReplyDto reply
) {

    public static List<AlarmFindResponse> toList(List<Alarm> alarms) {
        return alarms.stream()
                .map(alarm -> AlarmFindResponse.builder()
                        .id(alarm.getId())
                        .createdDate(alarm.getCreatedDate())
                        .isChecked(alarm.isChecked())
                        .type(alarm.getType().name())
                        .sendUser(UserDto.of(alarm.getSendUser()))
                        .list(ListDto.of(alarm.getList()))
                        .comment(CommentDto.of(alarm.getComment()))
                        .reply(ReplyDto.of(alarm.getReply()))
                        .build()
                ).toList();
    }

    public record UserDto(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

        public static UserDto of(User user) {
            return new UserDto(user.getId(), user.getNickname(), user.getProfileImageUrl());
        }
    }

    public record ListDto(
            Long id,
            String title
    ) {

        public static ListDto of(ListEntity list) {
            if (list == null) {
                return null;
            }
            return new ListDto(list.getId(), list.getTitle().getValue());
        }
    }

    public record CommentDto(
            Long id,
            String content,
            List<MentionDto> mentions
    ) {

        public static CommentDto of(Comment comment) {
            if (comment == null) {
                return null;
            }
            return new CommentDto(comment.getId(), comment.getCommentContent(), MentionDto.toList(comment.getMentions()));
        }
    }

    public record ReplyDto(
            Long id,
            String content,
            List<MentionDto> mentions
    ) {

        public static ReplyDto of(Reply reply) {
            if (reply == null) {
                return null;
            }
            return new ReplyDto(reply.getId(), reply.getCommentContent(), MentionDto.toList(reply.getMentions()));
        }
    }

    public record MentionDto(
            Long targetUserId,
            String targetUserNickname
    ) {

        public static List<MentionDto> toList(List<Mention> mentions) {
            if (mentions.isEmpty()) {
                return List.of();
            }
            return mentions.stream()
                    .map(mention -> new MentionDto(mention.getUser().getId(), mention.getUser().getNickname()))
                    .toList();
        }
    }

    // TODO: notice
}
