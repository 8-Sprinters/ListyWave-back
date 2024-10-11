package com.listywave.list.presentation.dto.request.comment;

import java.util.List;

public record CommentCreateRequest(
        String content,
        List<Long> mentionedIds
) {
}
