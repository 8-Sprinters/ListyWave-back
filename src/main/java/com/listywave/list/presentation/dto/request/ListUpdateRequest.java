package com.listywave.list.presentation.dto.request;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.BackgroundColor;
import com.listywave.list.application.domain.list.BackgroundPalette;
import java.util.List;

public record ListUpdateRequest(
        CategoryType category,
        List<String> labels,
        List<Long> collaboratorIds,
        String title,
        String description,
        boolean isPublic,
        BackgroundPalette backgroundPalette,
        BackgroundColor backgroundColor,
        List<ItemCreateRequest> items
) {
}
