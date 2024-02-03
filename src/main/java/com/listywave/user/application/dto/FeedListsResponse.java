package com.listywave.user.application.dto;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import java.util.stream.Collectors;

public record FeedListsResponse(
        Long id,
        String title,
        Boolean isPublic,
        List<ListItemsResponse> listItems
) {
    public static FeedListsResponse of(Lists lists) {
        return new FeedListsResponse(
                lists.getId(),
                lists.getTitle(),
                lists.isPublic(),
                lists.getItems().stream()
                        .map(ListItemsResponse::of)
                        .collect(Collectors.toList())
        );
    }
}

record ListItemsResponse(
        Long id,
        int ranking,
        String title,
        String imageUrl
) {
    public static ListItemsResponse of(Item item) {
        return new ListItemsResponse(
                item.getId(),
                item.getRanking(),
                item.getTitle(),
                item.getImageUrl()
        );
    }
}
