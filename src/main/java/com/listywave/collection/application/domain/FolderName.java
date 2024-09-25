package com.listywave.collection.application.domain;

import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class FolderName {

    private static final int LENGTH_LIMIT = 30;

    @Column(name = "name", nullable = false, length = LENGTH_LIMIT)
    private final String value;

    public FolderName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, "폴더 이름은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }
}
