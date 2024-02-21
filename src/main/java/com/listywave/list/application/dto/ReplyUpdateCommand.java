package com.listywave.list.application.dto;

public record ReplyUpdateCommand(
        Long listId,
        Long commentId,
        Long replyId,
        String content
) {
}
