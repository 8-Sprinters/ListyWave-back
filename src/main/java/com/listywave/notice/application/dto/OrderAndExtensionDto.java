package com.listywave.notice.application.dto;

import com.listywave.image.application.domain.ImageFileExtension;

public record OrderAndExtensionDto(
        int order,
        ImageFileExtension extension
) {
}
