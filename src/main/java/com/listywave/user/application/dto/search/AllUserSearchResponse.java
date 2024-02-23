package com.listywave.user.application.dto.search;

import java.util.List;
import lombok.Builder;

@Builder
public record AllUserSearchResponse(
        List<UserSearchResponse> users,
        Long totalCount,
        Boolean hasNext
) {

    public static AllUserSearchResponse of(
            List<UserSearchResponse> users,
            Long totalCount,
            Boolean hasNext
    ) {
        return AllUserSearchResponse.builder()
                .users(users)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }
}
