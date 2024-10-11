package com.listywave.list.presentation.dto.request;

import java.util.List;

public record ReplyCreateRequest(
        String content,
        List<Long> mentionedIds
) {
}
