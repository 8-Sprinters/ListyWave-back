package com.listywave.collection.application.dto;

import com.listywave.collection.application.domain.Collect;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record CollectionFindResponse(
        Long cursorId,
        Boolean hasNext,
        List<CollectionDto> collectionLists,
        String folderName
) {

    public static CollectionFindResponse of(
            Long cursorId,
            Boolean hasNext,
            List<Collect> collects,
            String folderName
    ) {
        return new CollectionFindResponse(cursorId, hasNext, toList(collects), folderName);
    }

    public static List<CollectionDto> toList(List<Collect> collects) {
        return collects.stream()
                .map(CollectionDto::of)
                .toList();
    }

    public record CollectionDto(
            Long id,
            ListsDto list
    ) {

        public static CollectionDto of(Collect collect) {
            return new CollectionDto(collect.getId(), toResponse(collect.getList()));
        }

        public static ListsDto toResponse(ListEntity list) {
            return ListsDto.of(list);
        }
    }

    @Builder
    public record ListsDto(
            Long id,
            String backgroundColor,
            String title,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImageUrl,
            String representativeImageUrl,
            String category,
            LocalDateTime updatedDate,
            List<ListItemsDto> listItems
    ) {

        public static ListsDto of(ListEntity list) {
            return ListsDto.builder()
                    .id(list.getId())
                    .backgroundColor(list.getBackgroundColor().name())
                    .title(list.getTitle().getValue())
                    .ownerId(list.getUser().getId())
                    .ownerNickname(list.getUser().getNickname())
                    .ownerProfileImageUrl(list.getUser().getProfileImageUrl())
                    .representativeImageUrl(list.getRepresentImageUrl())
                    .category(list.getCategory().getViewName())
                    .updatedDate(list.getUpdatedDate())
                    .listItems(toList(list.getTop3Items().getValues()))
                    .build();
        }

        public static List<ListItemsDto> toList(List<Item> items) {
            return items.stream()
                    .map(ListItemsDto::of)
                    .toList();
        }
    }

    @Builder
    public record ListItemsDto(
            Long id,
            int rank,
            String title,
            String imageUrl
    ) {

        public static ListItemsDto of(Item item) {
            return ListItemsDto.builder()
                    .id(item.getId())
                    .rank(item.getRanking())
                    .title(item.getTitle().getValue())
                    .imageUrl(item.getImageUrl().getValue())
                    .build();
        }
    }
}
