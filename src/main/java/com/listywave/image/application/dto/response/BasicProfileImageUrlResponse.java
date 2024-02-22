package com.listywave.image.application.dto.response;

import com.listywave.image.application.domain.BasicProfileImage;

public record BasicProfileImageUrlResponse(
        String name,
        String imageUrl
) {

    public static BasicProfileImageUrlResponse of(BasicProfileImage basicProfileImage) {
        return new BasicProfileImageUrlResponse(
                basicProfileImage.name().toLowerCase(),
                basicProfileImage.getValue()
        );
    }
}
