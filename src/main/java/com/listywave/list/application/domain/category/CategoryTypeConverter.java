package com.listywave.list.application.domain.category;

import jakarta.persistence.AttributeConverter;

public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

    @Override
    public String convertToDatabaseColumn(CategoryType categoryType) {
        if (categoryType == null) {
            throw new NullPointerException("CategoryType을 DB 칼럼으로 변경하는 과정에서 null이 포함되었습니다.");
        }
        return categoryType.getCodeValue();
    }

    @Override
    public CategoryType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new NullPointerException("List 테이블의 category_id 값이 null입니다.");
        }
        return CategoryType.enumOf(dbData);
    }
}
