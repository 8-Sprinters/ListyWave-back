package com.listywave.user.application.dto;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FeedListsResponse(
        Long id,
        String title,
        Boolean isPublic,
        String backgroundColor,
        List<ListItemsResponse> listItems
) {

    public static FeedListsResponse of(Lists lists) {
        return FeedListsResponse.builder()
                .id(lists.getId())
                .title(lists.getTitle())
                .isPublic(lists.isPublic())
                .backgroundColor(lists.getBackgroundColor())
                .listItems(ListItemsResponse.toList(lists))
                .build();
    }
}

@Builder
record ListItemsResponse(
        Long id,
        int rank,
        String title,
        String imageUrl
) {

    public static ListItemsResponse of(Item item) {
        return ListItemsResponse.builder()
                .id(item.getId())
                .rank(item.getRanking())
                .title(item.getTitle())
                .imageUrl(item.getImageUrl())
                .build();
    }

    public static List<ListItemsResponse> toList(Lists lists) {
        return lists.getItems().stream()
                .map(ListItemsResponse::of)
                .collect(Collectors.toList());
    }
}
