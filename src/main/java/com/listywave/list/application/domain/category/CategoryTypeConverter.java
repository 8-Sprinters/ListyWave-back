package com.listywave.list.application.domain.category;

import jakarta.persistence.AttributeConverter;

public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {

    @Override
    public String convertToDatabaseColumn(CategoryType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCodeValue();
    }

    @Override
    public CategoryType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return CategoryType.enumOf(dbData);
    }
}
