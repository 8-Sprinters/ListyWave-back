package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record ListRecentResponse(
        boolean hasNext,
        Long cursorId,
        List<ListResponse> lists
) {
    public static ListRecentResponse of(List<ListEntity> lists, Long cursorId, boolean hasNext) {
        return ListRecentResponse.builder()
                .hasNext(hasNext)
                .cursorId(cursorId)
                .lists(ListResponse.toList(lists))
                .build();
    }

    @Builder
    public record ListResponse(
            Long id,
            String category,
            String backgroundColor,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImage,
            List<LabelsResponse> labels,
            String title,
            String description,
            List<ItemsResponse> items
    ) {

        public static List<ListResponse> toList(List<ListEntity> lists) {
            return lists.stream()
                    .map(ListResponse::of)
                    .toList();
        }

        public static ListResponse of(ListEntity list) {
            return ListResponse.builder()
                    .id(list.getId())
                    .category(list.getCategory().getViewName())
                    .backgroundColor(list.getBackgroundColor())
                    .ownerId(list.getUser().getId())
                    .ownerNickname(list.getUser().getNickname())
                    .ownerProfileImage(list.getUser().getProfileImageUrl())
                    .labels(LabelsResponse.toList(list.getLabels().getValues()))
                    .title(list.getTitle().getValue())
                    .description(list.getDescription().getValue())
                    .items(ItemsResponse.toList(list.getTop3Items().getValues()))
                    .build();
        }
    }

    @Builder
    public record LabelsResponse(
            Long id,
            String name
    ) {

        public static List<LabelsResponse> toList(List<Label> labels) {
            return labels.stream()
                    .map(LabelsResponse::of)
                    .toList();
        }

        public static LabelsResponse of(Label label) {
            return LabelsResponse.builder()
                    .id(label.getId())
                    .name(label.getName())
                    .build();
        }
    }

    @Builder
    public record ItemsResponse(
            Long id,
            int rank,
            String title,
            String imageUrl
    ) {

        public static List<ItemsResponse> toList(List<Item> items) {
            return items.stream()
                    .sorted(Comparator.comparing(Item::getRanking))
                    .map(ItemsResponse::of)
                    .limit(3)
                    .toList();
        }

        public static ItemsResponse of(Item item) {
            return ItemsResponse.builder()
                    .id(item.getId())
                    .rank(item.getRanking())
                    .title(item.getTitle().getValue())
                    .imageUrl(item.getImageUrl().getValue())
                    .build();
        }
    }

}
