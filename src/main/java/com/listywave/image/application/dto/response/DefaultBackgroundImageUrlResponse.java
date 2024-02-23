package com.listywave.image.application.dto.response;

import com.listywave.image.application.domain.DefaultBackgroundImages;

public record DefaultBackgroundImageUrlResponse(
        String name,
        String imageUrl
) {

    public static DefaultBackgroundImageUrlResponse of(DefaultBackgroundImages defaultBackgroundImage) {
        return new DefaultBackgroundImageUrlResponse(
                defaultBackgroundImage.name().toLowerCase(),
                defaultBackgroundImage.getValue()
        );
    }
}
