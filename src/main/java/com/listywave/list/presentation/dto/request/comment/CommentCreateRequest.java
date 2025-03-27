package com.listywave.list.presentation.dto.request.comment;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CommentCreateRequest(
        String content,
        @NotNull(message = "댓글 작성 시 멘션 ID에 Null이 될 수 없습니다.") List<Long> mentionIds
) {
}
