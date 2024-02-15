package com.listywave.list.application.dto;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.list.presentation.dto.request.ListCreateRequest;

public record ListCreateCommand(
        Long ownerId,
        CategoryType category,
        ListTitle title,
        ListDescription description,
        Boolean isPublic,
        String backgroundColor
) {

    public static ListCreateCommand of(ListCreateRequest listCreateRequest) {
        return new ListCreateCommand(
                listCreateRequest.ownerId(),
                listCreateRequest.category(),
                ListTitle.builder()
                        .value(listCreateRequest.title())
                        .build(),
                ListDescription.builder()
                        .value(listCreateRequest.description())
                        .build(),
                listCreateRequest.isPublic(),
                listCreateRequest.backgroundColor()
        );
    }
}
