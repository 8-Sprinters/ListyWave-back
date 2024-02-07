package com.listywave.user.application.dto;

import com.listywave.list.application.domain.Lists;
import java.util.List;

public record AllUserListsResponse(
        Long cursorId,
        Boolean hasNext,
        List<FeedListsResponse> feedLists
) {

    public static AllUserListsResponse of(boolean hasNext, Long cursorId, List<Lists> feedLists) {
        return new AllUserListsResponse(
                cursorId,
                hasNext,
                toList(feedLists)
        );
    }

    public static List<FeedListsResponse> toList(List<Lists> feedLists) {
        return feedLists.stream()
                .map(FeedListsResponse::of)
                .toList();
    }
}
