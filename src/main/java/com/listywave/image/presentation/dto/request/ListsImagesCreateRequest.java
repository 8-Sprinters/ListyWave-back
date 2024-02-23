package com.listywave.image.presentation.dto.request;

import com.listywave.image.application.dto.ExtensionRanks;
import java.util.List;

public record ListsImagesCreateRequest(
        Long listId,
        List<ExtensionRanks> extensionRanks
) {
}
