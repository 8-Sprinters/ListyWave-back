package com.listywave.image.application.dto.response;

import com.listywave.image.application.domain.BasicBackgroundImage;

public record BasicBackgroundImageUrlResponse(
        String name,
        String imageUrl
) {

    public static BasicBackgroundImageUrlResponse of(BasicBackgroundImage basicBackgroundImage) {
        return new BasicBackgroundImageUrlResponse(
                basicBackgroundImage.name().toLowerCase(),
                basicBackgroundImage.getValue()
        );
    }
}
