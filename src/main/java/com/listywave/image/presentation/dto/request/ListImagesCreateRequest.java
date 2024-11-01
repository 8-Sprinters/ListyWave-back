package com.listywave.image.presentation.dto.request;

import com.listywave.image.application.domain.ImageFileExtension;
import java.util.List;

public record ListImagesCreateRequest(
        Long listId,
        List<ExtensionRanks> extensionRanks
) {

    public record ExtensionRanks(
            int rank,
            ImageFileExtension extension
    ) {
    }
}
