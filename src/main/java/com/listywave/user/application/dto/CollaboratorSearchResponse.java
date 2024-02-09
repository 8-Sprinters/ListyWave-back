package com.listywave.user.application.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CollaboratorSearchResponse(
        List<CollaboratorResponse> collaborators,
        Long totalCount,
        Boolean hasNext
) {

    public static CollaboratorSearchResponse of(
            List<CollaboratorResponse> users,
            Long totalCount,
            Boolean hasNext
    ) {
        return CollaboratorSearchResponse.builder()
                .collaborators(users)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }
}
