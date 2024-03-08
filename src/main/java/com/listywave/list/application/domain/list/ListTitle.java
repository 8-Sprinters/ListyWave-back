package com.listywave.list.application.domain.list;

import static com.listywave.common.exception.ErrorCode.NICKNAME_LENGTH_EXCEEDED_EXCEPTION;
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
public class ListTitle {

    private static final int LENGTH_LIMIT = 30;

    @Column(name = "title", nullable = false, length = LENGTH_LIMIT)
    private final String value;

    public ListTitle(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(NICKNAME_LENGTH_EXCEEDED_EXCEPTION, "리스트 제목은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }

    public boolean isMatch(String keyword) {
        return match(value, keyword);
    }
}
