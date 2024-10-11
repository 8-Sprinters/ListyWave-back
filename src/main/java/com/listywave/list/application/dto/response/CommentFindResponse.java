package com.listywave.list.application.dto.response;

import static java.util.Collections.emptyList;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.mention.Mention;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record CommentFindResponse(
        Long totalCount,
        Long cursorId,
        boolean hasNext,
        List<CommentDto> comments
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
                .comments(CommentDto.toList(comments))
                .build();
    }

    @Builder
    public record CommentDto(
            Long id,
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            boolean isDeleted,
            List<ReplyDto> replies,
            List<MentionDto> mentions
    ) {

        public static List<CommentDto> toList(Map<Comment, List<Reply>> comments) {
            return comments.keySet().stream()
                    .map(comment -> CommentDto.of(comment, comments.get(comment)))
                    .toList();
        }

        public static CommentDto of(Comment comment, List<Reply> replies) {
            return CommentDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUserId())
                    .userNickname(comment.getUserNickname())
                    .userProfileImageUrl(comment.getUserProfileImageUrl())
                    .content(comment.getCommentContent())
                    .createdDate(comment.getCreatedDate())
                    .updatedDate(comment.getUpdatedDate())
                    .isDeleted(comment.isDeleted())
                    .replies(ReplyDto.toList(replies))
                    .mentions(MentionDto.toList(comment.getMentions()))
                    .build();
        }
    }

    @Builder
    public record ReplyDto(
            Long id,
            Long commentId,
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            String content,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            List<MentionDto> mentions
    ) {

        public static List<ReplyDto> toList(List<Reply> replies) {
            return replies.stream()
                    .map(ReplyDto::of)
                    .toList();
        }

        public static ReplyDto of(Reply reply) {
            return ReplyDto.builder()
                    .id(reply.getId())
                    .commentId(reply.getCommentId())
                    .userId(reply.getUserId())
                    .userNickname(reply.getUserNickname())
                    .userProfileImageUrl(reply.getUserProfileImageUrl())
                    .content(reply.getCommentContent())
                    .createdDate(reply.getCreatedDate())
                    .updatedDate(reply.getUpdatedDate())
                    .mentions(MentionDto.toList(reply.getMentions()))
                    .build();
        }
    }

    public record MentionDto(
            Long userId,
            String userNickname
    ) {

        public static List<MentionDto> toList(List<Mention> mentions) {
            return mentions.stream()
                    .map(Mention::getUser)
                    .filter(user -> !user.isDelete())
                    .map(user -> new MentionDto(user.getId(), user.getNickname()))
                    .toList();
        }
    }
}
