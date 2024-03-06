package com.listywave.user.application.dto.search;

import java.util.List;
import lombok.Builder;

@Builder
public record UserSearchResponse(
        List<UserSearchResult> users,
        Long totalCount,
        Boolean hasNext
) {

    public static UserSearchResponse of(
            List<UserSearchResult> users,
            Long totalCount,
            Boolean hasNext
    ) {
        return UserSearchResponse.builder()
                .users(users)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }
}
