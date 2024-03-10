package com.listywave.list.application.domain.list;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static com.listywave.common.util.StringUtils.match;

import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ListDescription {

    private static final int LENGTH_LIMIT = 200;

    @Column(name = "description", length = LENGTH_LIMIT, nullable = false)
    private final String value;

    public ListDescription(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value != null && value.length() > LENGTH_LIMIT) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, "리스트 설명은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }

    public boolean isMatch(String keyword) {
        return match(value, keyword);
    }
}
