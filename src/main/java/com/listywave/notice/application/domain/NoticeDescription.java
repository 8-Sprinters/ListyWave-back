package com.listywave.notice.application.domain;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.NULL_OR_BLANK_EXCEPTION;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED, force = true)
public class NoticeDescription {

    private static final int MAX_LENGTH = 30;

    @Column(name = "description", nullable = false, length = MAX_LENGTH)
    private final String value;

    public NoticeDescription(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(NULL_OR_BLANK_EXCEPTION, NULL_OR_BLANK_EXCEPTION.getDetail() + " 입력값: " + value);
        }
        if (value.length() > MAX_LENGTH) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, LENGTH_EXCEEDED_EXCEPTION.getDetail() + " 입력값의 길이: " + value.length());
        }
    }
}
