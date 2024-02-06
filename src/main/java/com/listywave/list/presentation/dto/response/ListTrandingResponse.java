package com.listywave.list.presentation.dto.response;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import lombok.Builder;

@Builder
public record ListTrandingResponse(
        Long id,
        String title,
        String description,
        String itemImageUrl,
        String backgroundColor
) {
    public static ListTrandingResponse of(Lists list) {
        return ListTrandingResponse.builder()
                .id(list.getId())
                .title(list.getTitle())
                .description(list.getDescription())
                .itemImageUrl(getImageUrlTopRankItem(list.getItems()))
                .backgroundColor(list.getBackgroundColor())
                .build();
    }

    public static String getImageUrlTopRankItem(List<Item> items) {
        return items.stream()
                .map(item -> item.getImageUrl())
                .filter(url -> !url.isEmpty())
                .findFirst()
                .orElse("");
    }
}
