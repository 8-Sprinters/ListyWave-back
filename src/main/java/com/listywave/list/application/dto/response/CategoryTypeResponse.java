package com.listywave.list.application.dto.response;

import com.listywave.list.application.domain.category.CategoryType;

public record CategoryTypeResponse(
        String codeValue,
        String nameValue,
        String korNameValue
) {

    public static CategoryTypeResponse fromEnum(CategoryType categoryType) {
        return new CategoryTypeResponse(
                categoryType.getCodeValue(),
                categoryType.name().toLowerCase(),
                categoryType.getKorNameValue()
        );
    }
}
