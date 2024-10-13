package com.listywave.list.presentation.dto.request.comment;

import java.util.List;

public record CommentUpdateRequest(
        String content,
        List<Long> mentionIds
) {
}
