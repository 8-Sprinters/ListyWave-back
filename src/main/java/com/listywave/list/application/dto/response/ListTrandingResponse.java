package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.list.BackgroundColor;

public record ListTrandingResponse(
        Long id,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        String title,
        String description,
        BackgroundColor backgroundColor,
        Long trandingScore,
        String itemImageUrl
) {

    public ListTrandingResponse(
            Long id,
            Long ownerId,
            String ownerNickname,
            String ownerProfileImageUrl,
            String title,
            String description,
            BackgroundColor backgroundColor,
            Long trandingScore
    ) {
        this(id, ownerId, ownerNickname, ownerProfileImageUrl, title, description, backgroundColor, trandingScore, "");
    }

    public ListTrandingResponse with(String imageUrl) {
        return new ListTrandingResponse(id, ownerId, ownerNickname, ownerProfileImageUrl, title, description, backgroundColor, trandingScore, imageUrl);
    }
}
