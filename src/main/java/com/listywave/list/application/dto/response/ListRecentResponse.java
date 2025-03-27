package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ListRecentResponse(
        boolean hasNext,
        LocalDateTime cursorUpdatedDate,
        List<ListResponse> lists
) {
    public static ListRecentResponse of(List<ListEntity> lists, LocalDateTime cursorUpdatedDate, boolean hasNext) {
        return ListRecentResponse.builder()
                .hasNext(hasNext)
                .cursorUpdatedDate(cursorUpdatedDate)
                .lists(ListResponse.toList(lists))
                .build();
    }

    @Builder
    public record ListResponse(
            Long id,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImage,
            String title,
            String description,
            List<ItemsResponse> items,
            int updateCount
    ) {

        public static List<ListResponse> toList(List<ListEntity> lists) {
            return lists.stream()
                    .map(ListResponse::of)
                    .toList();
        }

        public static ListResponse of(ListEntity list) {
            return ListResponse.builder()
                    .id(list.getId())
                    .ownerId(list.getUser().getId())
                    .ownerNickname(list.getUser().getNickname())
                    .ownerProfileImage(list.getUser().getProfileImageUrl())
                    .title(list.getTitle().getValue())
                    .description(list.getDescription().getValue())
                    .items(ItemsResponse.toList(list.getTop3Items().getValues()))
                    .updateCount(list.getUpdateCount())
                    .build();
        }
    }

    @Builder
    public record ItemsResponse(
            Long id,
            int rank,
            String title
    ) {

        public static List<ItemsResponse> toList(List<Item> items) {
            return items.stream()
                    .map(ItemsResponse::of)
                    .toList();
        }

        public static ItemsResponse of(Item item) {
            return ItemsResponse.builder()
                    .id(item.getId())
                    .rank(item.getRanking())
                    .title(item.getTitle().getValue())
                    .build();
        }
    }
}

