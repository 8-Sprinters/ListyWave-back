package com.listywave.list.application.dto;

public record ReplyDeleteCommand(
        Long listId,
        Long commentId,
        Long replyId,
        String accessToken
) {
}
