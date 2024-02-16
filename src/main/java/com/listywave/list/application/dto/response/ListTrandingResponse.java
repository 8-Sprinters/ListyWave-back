package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.list.ListEntity;
import lombok.Builder;

@Builder
public record ListTrandingResponse(
        Long id,
        Long ownerId,
        String title,
        String description,
        String itemImageUrl,
        String backgroundColor
) {

    public static ListTrandingResponse of(ListEntity list, String imageUrlTopRankItem) {
        return ListTrandingResponse.builder()
                .id(list.getId())
                .ownerId(list.getUser().getId())
                .title(list.getTitle().getValue())
                .description(list.getDescription().getValue())
                .itemImageUrl(imageUrlTopRankItem)
                .backgroundColor(list.getBackgroundColor())
                .build();
    }
}
