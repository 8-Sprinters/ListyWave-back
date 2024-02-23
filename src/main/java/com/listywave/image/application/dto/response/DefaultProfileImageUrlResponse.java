package com.listywave.image.application.dto.response;

import com.listywave.image.application.domain.DefaultProfileImages;

public record DefaultProfileImageUrlResponse(
        String name,
        String imageUrl
) {

    public static DefaultProfileImageUrlResponse of(DefaultProfileImages defaultProfileImage) {
        return new DefaultProfileImageUrlResponse(
                defaultProfileImage.name().toLowerCase(),
                defaultProfileImage.getValue()
        );
    }
}
