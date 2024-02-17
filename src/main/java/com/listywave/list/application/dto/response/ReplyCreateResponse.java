package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.user.application.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReplyCreateResponse(
        Long id,
        Long commentId,
        Long userId,
        String userNickname,
        String userProfileImageUrl,
        String content,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {

    public static ReplyCreateResponse of(Reply reply, Comment comment, User user) {
        return ReplyCreateResponse.builder()
                .id(reply.getId())
                .commentId(comment.getId())
                .userId(user.getId())
                .userNickname(user.getNickname())
                .userProfileImageUrl(user.getProfileImageUrl())
                .content(reply.getCommentContent())
                .createdDate(reply.getCreatedDate())
                .updatedDate(reply.getUpdatedDate())
                .build();
    }
}
