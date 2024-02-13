package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
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

    public static ListSearchResponse of(java.util.List<Lists> lists, Long totalCount, Long cursorId, boolean hasNext) {
        return ListSearchResponse.builder()
                .resultLists(ListInfo.toList(lists))
                .totalCount(totalCount)
                .cursorId(cursorId)
                .hasNext(hasNext)
                .build();
    }
}

@Builder
record ListInfo(
        Long id,
        String title,
        java.util.List<ItemInfo> items,
        boolean isPublic,
        String backgroundColor,
        LocalDateTime updatedDate,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl
) {

    public static List<ListInfo> toList(java.util.List<Lists> lists) {
        return lists.stream()
                .map(ListInfo::of)
                .toList();
    }

    private static ListInfo of(Lists lists) {
        return ListInfo.builder()
                .id(lists.getId())
                .title(lists.getTitle())
                .items(ItemInfo.toList(lists.getItems()))
                .isPublic(lists.isPublic())
                .backgroundColor(lists.getBackgroundColor())
                .updatedDate(lists.getUpdatedDate())
                .ownerId(lists.getUser().getId())
                .ownerNickname(lists.getUser().getNickname())
                .ownerProfileImageUrl(lists.getUser().getProfileImageUrl())
                .build();
    }
}

@Builder
record ItemInfo(
        Long id,
        String title
) {

    public static java.util.List<ItemInfo> toList(java.util.List<Item> items) {
        return items.stream()
                .map(ItemInfo::of)
                .toList();
    }

    private static ItemInfo of(Item item) {
        return ItemInfo.builder()
                .id(item.getId())
                .title(item.getTitle())
                .build();
    }
}
