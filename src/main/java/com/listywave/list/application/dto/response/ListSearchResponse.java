package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ListSearchResponse(
        List<ListInfo> resultLists,
        Long totalCount,
        Long cursorId,
        boolean hasNext
) {

    public static ListSearchResponse of(
            List<ListEntity> lists,
            Long totalCount,
            Long cursorId,
            boolean hasNext
    ) {
        return ListSearchResponse.builder()
                .resultLists(ListInfo.toList(lists))
                .totalCount(totalCount)
                .cursorId(cursorId)
                .hasNext(hasNext)
                .build();
    }

    @Builder
    public record ListInfo(
            Long id,
            String title,
            List<ItemInfo> items,
            boolean isPublic,
            String backgroundColor,
            LocalDateTime updatedDate,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImageUrl,
            String representImageUrl
    ) {

        public static List<ListInfo> toList(List<ListEntity> lists) {
            return lists.stream()
                    .map(ListInfo::of)
                    .toList();
        }

        private static ListInfo of(ListEntity list) {
            return ListInfo.builder()
                    .id(list.getId())
                    .title(list.getTitle().getValue())
                    .items(ItemInfo.toList(list.getTop3Items().getValues()))
                    .isPublic(list.isPublic())
                    .backgroundColor(list.getBackgroundColor().name())
                    .updatedDate(list.getUpdatedDate())
                    .ownerId(list.getUser().getId())
                    .ownerNickname(list.getUser().getNickname())
                    .ownerProfileImageUrl(list.getUser().getProfileImageUrl())
                    .representImageUrl(list.getRepresentImageUrl())
                    .build();
        }
    }

    @Builder
    public record ItemInfo(
            Long id,
            String title
    ) {

        public static List<ItemInfo> toList(List<Item> items) {
            return items.stream()
                    .map(ItemInfo::of)
                    .toList();
        }

        private static ItemInfo of(Item item) {
            return ItemInfo.builder()
                    .id(item.getId())
                    .title(item.getTitle().getValue())
                    .build();
        }
    }
}
