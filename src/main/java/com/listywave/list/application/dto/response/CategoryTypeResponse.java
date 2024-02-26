package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.category.CategoryType;

public record CategoryTypeResponse(
        String codeValue,
        String nameValue,
        String korNameValue,
        String categoryImageUrl
) {

    public static CategoryTypeResponse of(CategoryType categoryType) {
        return new CategoryTypeResponse(
                categoryType.getCode(),
                categoryType.name().toLowerCase(),
                categoryType.getViewName(),
                categoryType.getImageUrl()
        );
    }
}
