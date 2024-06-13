package com.listywave.user.application.dto.search;

import java.util.List;
import lombok.Builder;

@Builder
public record UserElasticSearchResponse(
        List<UserElasticSearchResult> users,
        Long totalCount,
        Boolean hasNext
) {

    public static UserElasticSearchResponse of(
            List<UserElasticSearchResult> users,
            Long totalCount,
            Boolean hasNext
    ) {
        return UserElasticSearchResponse.builder()
                .users(users)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }
}
