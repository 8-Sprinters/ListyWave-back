package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Label;
import com.listywave.list.application.domain.Lists;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;

@Builder
public record ListRecentResponse(
        List<ListResponse> lists
) {
    public static ListRecentResponse of(List<Lists> lists) {
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

    public static ListResponse of(Lists lists) {
        return ListResponse.builder()
                .id(lists.getId())
                .category(lists.getCategory().getKorNameValue())
                .backgroundColor(lists.getBackgroundColor())
                .ownerId(lists.getUser().getId())
                .ownerNickname(lists.getUser().getNickname())
                .ownerProfileImage(lists.getUser().getProfileImageUrl())
                .labels(LabelsResponse.toList(lists.getLabels()))
                .title(lists.getTitle())
                .description(lists.getDescription())
                .items(ItemsResponse.toList(lists.getItems()))
                .build();
    }

    public static List<ListResponse> toList(List<Lists> lists) {
        return lists.stream()
                .map(ListResponse::of)
                .toList();
    }
}

@Builder
record LabelsResponse(
        Long id,
        String name
) {

    public static LabelsResponse of(Label label) {
        return LabelsResponse.builder()
                .id(label.getId())
                .name(label.getLabelName())
                .build();
    }

    public static List<LabelsResponse> toList(List<Label> labels) {
        return labels.stream()
                .map(LabelsResponse::of)
                .toList();
    }
}

@Builder
record ItemsResponse(
        Long id,
        int ranking,
        String title,
        String imageUrl
) {

    public static ItemsResponse of(Item item) {
        return ItemsResponse.builder()
                .id(item.getId())
                .ranking(item.getRanking())
                .title(item.getTitle())
                .imageUrl(item.getImageUrl())
                .build();
    }

    public static List<ItemsResponse> toList(List<Item> items) {
        return items.stream()
                .sorted(Comparator.comparing(Item::getRanking))
                .map(ItemsResponse::of)
                .limit(3)
                .toList();
    }
}
