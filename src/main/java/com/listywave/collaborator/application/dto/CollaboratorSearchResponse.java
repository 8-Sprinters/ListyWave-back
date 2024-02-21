package com.listywave.collaborator.application.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CollaboratorSearchResponse(
        List<CollaboratorResponse> users,
        Long totalCount,
        Boolean hasNext
) {

    public static CollaboratorSearchResponse of(
            List<CollaboratorResponse> users,
            Long totalCount,
            Boolean hasNext
    ) {
        return CollaboratorSearchResponse.builder()
                .users(users)
                .totalCount(totalCount)
                .hasNext(hasNext)
                .build();
    }
}
