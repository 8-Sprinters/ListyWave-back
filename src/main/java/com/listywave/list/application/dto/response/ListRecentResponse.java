package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record ListRecentResponse(
        List<ListResponse> lists
) {
    public static ListRecentResponse of(List<ListEntity> lists) {
        return ListRecentResponse.builder()
                .lists(ListResponse.toList(lists))
                .build();
    }
}

@Builder
record ListResponse(
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
                .category(list.getCategory().getKorNameValue())
                .backgroundColor(list.getBackgroundColor())
                .ownerId(list.getUser().getId())
                .ownerNickname(list.getUser().getNickname())
                .ownerProfileImage(list.getUser().getProfileImageUrl())
                .labels(LabelsResponse.toList(list.getLabels()))
                .title(list.getTitle())
                .description(list.getDescription())
                .items(ItemsResponse.toList(list.getItems()))
                .build();
    }
}

@Builder
record LabelsResponse(
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
                .name(label.getLabelName())
                .build();
    }
}

@Builder
record ItemsResponse(
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
                .title(item.getTitle())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
