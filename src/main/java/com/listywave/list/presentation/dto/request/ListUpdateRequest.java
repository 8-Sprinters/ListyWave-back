package com.listywave.list.presentation.dto.request;

import com.listywave.list.application.domain.category.CategoryType;
import java.util.List;

public record ListUpdateRequest(
        CategoryType category,
        List<String> labels,
        List<Long> collaboratorIds,
        String title,
        String description,
        boolean isPublic,
        String backgroundColor,
        List<ItemCreateRequest> items
) {
}
