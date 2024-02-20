package com.listywave.history.application.domain;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class HistoryItemTitle {

    private static final int MAX_LENGTH = 100;

    @Column(name = "title", length = MAX_LENGTH, updatable = false)
    private final String value;

    public HistoryItemTitle(String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new CustomException(LENGTH_EXCEEDED, "아이템 제목은 " + MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
    }
}
