package com.listywave.notice.application.converter;

import com.listywave.notice.application.domain.NoticeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NoticeTypeConverter implements AttributeConverter<NoticeType, String> {

    @Override
    public String convertToDatabaseColumn(NoticeType noticeType) {
        return String.valueOf(noticeType.getCode());
    }

    @Override
    public NoticeType convertToEntityAttribute(String s) {
        return NoticeType.codeOf(Integer.parseInt(s));
    }
}
