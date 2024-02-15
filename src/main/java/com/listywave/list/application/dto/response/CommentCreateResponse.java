package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.user.application.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentCreateResponse(
        Long id,
        Long userId,
        String userNickname,
        String userProfileImageUrl,
        String content,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {

    public static CommentCreateResponse of(Comment comment, User user) {
        return CommentCreateResponse.builder()
                .id(comment.getId())
                .userId(user.getId())
                .userNickname(user.getNickname())
                .userProfileImageUrl(user.getProfileImageUrl())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .updatedDate(comment.getUpdatedDate())
                .build();
    }
}
