package com.listywave.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute) {
            return "true";
        }
        return "false";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if ("true".equalsIgnoreCase(dbData)) {
            return true;
        }
        return false;
    }
}
