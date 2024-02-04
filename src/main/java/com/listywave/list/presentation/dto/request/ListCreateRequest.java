package com.listywave.list.presentation.dto.request;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.vo.ListDescription;
import com.listywave.list.application.vo.ListTitle;
import java.util.List;

public record ListCreateRequest(
        Long ownerId,
        CategoryType category,
        List<String> labels,
        List<Long> collaboratorIds,
        String title,
        String description,
        Boolean isPublic,
        String backgroundColor,
        List<ItemCreateRequest> items
) {

    public ListCreateCommand toCommand() {
        return new ListCreateCommand(
                ownerId,
                category,
                ListTitle.builder()
                        .value(title)
                        .build(),
                ListDescription.builder()
                        .value(description)
                        .build(),
                isPublic,
                backgroundColor
        );
    }
}
