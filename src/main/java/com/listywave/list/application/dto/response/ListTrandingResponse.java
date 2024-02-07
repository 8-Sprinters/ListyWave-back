package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.Lists;
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

    public static ListTrandingResponse of(Lists list, String imageUrlTopRankItem) {
        return ListTrandingResponse.builder()
                .id(list.getId())
                .ownerId(list.getUser().getId())
                .title(list.getTitle())
                .description(list.getDescription())
                .itemImageUrl(imageUrlTopRankItem)
                .backgroundColor(list.getBackgroundColor())
                .build();
    }
}
