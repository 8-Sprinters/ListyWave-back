package com.listywave.image.presentation.dto.request;

import com.listywave.image.application.domain.ImageFileExtension;

public record UserImageUpdateRequest(
        ImageFileExtension profileExtension,
        ImageFileExtension backgroundExtension
) {
}
