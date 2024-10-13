package com.listywave.list.application.dto;

import java.util.List;

public record ReplyUpdateCommand(
        Long listId,
        Long commentId,
        Long replyId,
        String content,
        List<Long> mentionIds
) {
}
