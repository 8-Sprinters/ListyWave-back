package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.list.ListEntity;
import lombok.Builder;

@Builder
public record ListTrandingResponse(
        Long id,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        String title,
        String description,
        String itemImageUrl,
        String backgroundColor
) {

    public static ListTrandingResponse of(ListEntity list) {
        return ListTrandingResponse.builder()
                .id(list.getId())
                .ownerId(list.getUser().getId())
                .ownerNickname(list.getUser().getNickname())
                .ownerProfileImageUrl(list.getUser().getProfileImageUrl())
                .title(list.getTitle().getValue())
                .description(list.getDescription().getValue())
                .itemImageUrl(list.getRepresentImageUrl())
                .backgroundColor(list.getBackgroundColor())
                .build();
    }
}
