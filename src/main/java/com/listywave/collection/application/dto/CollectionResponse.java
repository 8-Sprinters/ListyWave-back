package com.listywave.collection.application.dto;

import com.listywave.collection.application.domain.Collect;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

public record CollectionResponse(
        Long cursorId,
        Boolean hasNext,
        List<CollectionListsResponse> collectionLists
) {

    public static CollectionResponse of(Long cursorId, Boolean hasNext, List<Collect> collects) {
        return new CollectionResponse(cursorId, hasNext, toList(collects));
    }

    public static List<CollectionListsResponse> toList(List<Collect> collects) {
        return collects.stream()
                .map(CollectionListsResponse::of)
                .toList();
    }

    public record CollectionListsResponse(
            Long id,
            ListsResponse list
    ) {

        public static CollectionListsResponse of(Collect collect) {
            return new CollectionListsResponse(collect.getId(), toResponse(collect.getList()));
        }

        public static ListsResponse toResponse(ListEntity list) {
            return ListsResponse.of(list);
        }
    }

    @Builder
    public record ListsResponse(
            Long id,
            String backgroundColor,
            String title,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImageUrl,
            LocalDateTime updatedDate,
            List<ListItemsResponse> listItems
    ) {

        public static ListsResponse of(ListEntity list) {
            return ListsResponse.builder()
                    .id(list.getId())
                    .backgroundColor(list.getBackgroundColor())
                    .title(list.getTitle().getValue())
                    .ownerId(list.getUser().getId())
                    .ownerNickname(list.getUser().getNickname())
                    .ownerProfileImageUrl(list.getUser().getProfileImageUrl())
                    .updatedDate(list.getUpdatedDate())
                    .listItems(toList(list.getTop3Items().getValues()))
                    .build();
        }

        public static List<ListItemsResponse> toList(List<Item> items) {
            return items.stream()
                    .sorted(Comparator.comparing(Item::getRanking))
                    .map(ListItemsResponse::of)
                    .limit(3)
                    .toList();
        }
    }

    @Builder
    public record ListItemsResponse(
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
    }
}
