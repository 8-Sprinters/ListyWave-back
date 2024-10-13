package com.listywave.list.presentation.dto.request;

import java.util.List;

public record ReplyUpdateRequest(
        String content,
        List<Long> mentionIds
) {
}
