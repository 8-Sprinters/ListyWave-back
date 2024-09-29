package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendedListResponse(
        Long id,
        Long ownerId,
        String ownerNickname,
        String title,
        String itemImageUrl,
        String category,
        String backgroundColor,
        List<Top3ItemResponse> items
) {
    public static RecommendedListResponse of(ListEntity list) {
        return RecommendedListResponse.builder()
                .id(list.getId())
                .ownerId(list.getUser().getId())
                .ownerNickname(list.getUser().getNickname())
                .title(list.getTitle().getValue())
                .itemImageUrl(list.getRepresentImageUrl())
                .category(list.getCategory().getViewName())
                .backgroundColor(list.getBackgroundColor().name())
                .items(Top3ItemResponse.toList(list.getTop3Items().getValues()))
                .build();
    }

    @Builder
    public record Top3ItemResponse(
            Long id,
            int rank,
            String title
    ) {

        public static List<Top3ItemResponse> toList(List<Item> items) {
            return items.stream()
                    .map(Top3ItemResponse::of)
                    .toList();
        }

        public static Top3ItemResponse of(Item item) {
            return Top3ItemResponse.builder()
                    .id(item.getId())
                    .rank(item.getRanking())
                    .title(item.getTitle().getValue())
                    .build();
        }
    }
}
