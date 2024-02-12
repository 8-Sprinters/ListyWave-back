package com.listywave.image.presentation.dto.request;

import com.listywave.image.application.domain.ImageFileExtension;

public record UserImageUpdateRequest(
        Long ownerId,
        ImageFileExtension profileExtension,
        ImageFileExtension backgroundExtension
) {
}
