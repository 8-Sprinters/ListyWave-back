package com.listywave.user.application.dto;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record AllListOfUserSearchResponse(
        LocalDateTime cursorUpdatedDate,
        Boolean hasNext,
        List<FeedListsResponse> feedLists
) {

    public static AllListOfUserSearchResponse of(boolean hasNext, LocalDateTime cursorUpdatedDate, List<ListEntity> feedLists) {
        return new AllListOfUserSearchResponse(
                cursorUpdatedDate,
                hasNext,
                toList(feedLists)
        );
    }

    public static List<FeedListsResponse> toList(List<ListEntity> feedLists) {
        return feedLists.stream()
                .map(FeedListsResponse::of)
                .toList();
    }

    @Builder
    public record FeedListsResponse(
            Long id,
            String title,
            Boolean isPublic,
            String backgroundColor,
            List<ListItemsResponse> listItems
    ) {

        public static FeedListsResponse of(ListEntity list) {
            return FeedListsResponse.builder()
                    .id(list.getId())
                    .title(list.getTitle().getValue())
                    .isPublic(list.isPublic())
                    .backgroundColor(list.getBackgroundColor())
                    .listItems(ListItemsResponse.toList(list.getSortedItems().getValues()))
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

        public static List<ListItemsResponse> toList(List<Item> items) {
            return items.stream()
                    .map(ListItemsResponse::of)
                    .toList();
        }

        public static ListItemsResponse of(Item item) {
            return ListItemsResponse.builder()
                    .id(item.getId())
                    .rank(item.getRanking())
                    .title(item.getTitle().getValue())
                    .imageUrl(item.getImageUrl().getValue())
                    .build();
        }
    }

}
