package com.listywave.list.application.dto.response;

import static java.util.Collections.emptyList;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record CommentFindResponse(
        Long totalCount,
        Long cursorId,
        boolean hasNext,
        List<CommentResponse> comments
) {

    public static CommentFindResponse emptyResponse() {
        return CommentFindResponse.builder()
                .totalCount(0L)
                .cursorId(0L)
                .hasNext(false)
                .comments(emptyList())
                .build();
    }

    public static CommentFindResponse from(
            Long totalCount,
            Long cursorId,
            boolean hasNext,
            Map<Comment, List<Reply>> comments
    ) {
        return CommentFindResponse.builder()
                .totalCount(totalCount)
                .cursorId(cursorId)
                .hasNext(hasNext)
                .comments(CommentResponse.toList(comments))
                .build();
    }

    @Builder
    public record CommentResponse(
            Long id,
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            boolean isDeleted,
            List<ReplyResponse> replies
    ) {

        public static List<CommentResponse> toList(Map<Comment, List<Reply>> comments) {
            return comments.keySet().stream()
                    .map(comment -> CommentResponse.of(comment, comments.get(comment)))
                    .toList();
        }

        public static CommentResponse of(Comment comment, List<Reply> replies) {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .userId(comment.getUserId())
                    .userNickname(comment.getUserNickname())
                    .userProfileImageUrl(comment.getUserProfileImageUrl())
                    .content(comment.getCommentContent())
                    .createdDate(comment.getCreatedDate())
                    .updatedDate(comment.getUpdatedDate())
                    .isDeleted(comment.isDeleted())
                    .replies(ReplyResponse.toList(replies))
                    .build();
        }
    }

    @Builder
    public record ReplyResponse(
            Long id,
            Long commentId,
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate
    ) {

        public static List<ReplyResponse> toList(List<Reply> replies) {
            return replies.stream()
                    .map(ReplyResponse::of)
                    .toList();
        }

        public static ReplyResponse of(Reply reply) {
            return ReplyResponse.builder()
                    .id(reply.getId())
                    .commentId(reply.getCommentId())
                    .userId(reply.getUserId())
                    .userNickname(reply.getUserNickname())
                    .userProfileImageUrl(reply.getUserProfileImageUrl())
                    .content(reply.getCommentContent())
                    .createdDate(reply.getCreatedDate())
                    .updatedDate(reply.getUpdatedDate())
                    .build();
        }
    }
}
