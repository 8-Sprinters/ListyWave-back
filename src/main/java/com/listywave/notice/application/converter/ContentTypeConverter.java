package com.listywave.notice.application.converter;


import com.listywave.notice.application.domain.ContentType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ContentTypeConverter implements AttributeConverter<ContentType, String> {

    @Override
    public String convertToDatabaseColumn(ContentType contentType) {
        return contentType.name().toLowerCase();
    }

    @Override
    public ContentType convertToEntityAttribute(String s) {
        return ContentType.valueOf(s.toUpperCase());
    }
}
