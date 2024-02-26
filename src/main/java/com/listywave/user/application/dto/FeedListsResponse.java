package com.listywave.user.application.dto;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record FeedListsResponse(
        Long id,
        String title,
        Boolean isPublic,
        Long ownerId,
        String backgroundColor,
        List<ListItemsResponse> listItems
) {

    public static FeedListsResponse of(ListEntity list) {
        return FeedListsResponse.builder()
                .id(list.getId())
                .title(list.getTitle().getValue())
                .ownerId(list.getUser().getId())
                .isPublic(list.isPublic())
                .backgroundColor(list.getBackgroundColor())
                .listItems(ListItemsResponse.toList(list))
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
                .title(item.getTitle().getValue())
                .imageUrl(item.getImageUrl().getValue())
                .build();
    }

    public static List<ListItemsResponse> toList(ListEntity list) {
        return list.getItems().getValues().stream()
                .map(ListItemsResponse::of)
                .collect(Collectors.toList());
    }
}
