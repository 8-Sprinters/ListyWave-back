package com.listywave.user.presentation.dto.response;

import com.listywave.user.application.dto.FeedListsDto;
import java.util.List;
import lombok.Builder;

public record AllUserListsResponse(
    Long cursorId,
    Boolean hasNext,
    List<FeedListsDto> feedLists
) {
    public static AllUserListsResponse of(boolean hasNext, Long cursorId, List<FeedListsDto> lists){
        return new AllUserListsResponse(
                cursorId,
                hasNext,
                lists
        );
    }
}
