package com.listywave.user.application.dto;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record FindFeedListResponse(
        LocalDateTime cursorUpdatedDate,
        Boolean hasNext,
        List<FeedListInfo> feedLists
) {

    public static FindFeedListResponse of(boolean hasNext, LocalDateTime cursorUpdatedDate, List<ListEntity> feedLists) {
        return new FindFeedListResponse(
                cursorUpdatedDate,
                hasNext,
                toList(feedLists)
        );
    }

    public static List<FeedListInfo> toList(List<ListEntity> feedLists) {
        return feedLists.stream()
                .map(FeedListInfo::of)
                .toList();
    }

    @Builder
    public record FeedListInfo(
            Long id,
            String title,
            Boolean isPublic,
            String backgroundColor,
            List<ListItemsResponse> listItems
    ) {

        public static FeedListInfo of(ListEntity list) {
            return FeedListInfo.builder()
                    .id(list.getId())
                    .title(list.getTitle().getValue())
                    .isPublic(list.isPublic())
                    .backgroundColor(list.getBackgroundColor())
                    .listItems(ListItemsResponse.toList(list.getSortedItems().getValues()))
                    .build();
        }
    }

    @Builder
    public record ListItemsResponse(
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
