package com.listywave.list.application.dto.response;

import lombok.Builder;

@Builder
public record ReactionResponse(
        String reaction,
        Integer count,
        boolean isReacted
) {

    public static ReactionResponse of(String name, Integer count, boolean isReacted) {
        return ReactionResponse.builder()
                .reaction(name)
                .count(count)
                .isReacted(isReacted)
                .build();
    }
}
