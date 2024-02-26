package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED;

import com.listywave.common.exception.CustomException;
import com.listywave.common.util.StringUtils;
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
public class ItemTitle {

    private static final int MAX_LENGTH = 100;

    @Column(name = "title", nullable = false, length = MAX_LENGTH)
    private final String value;

    public ItemTitle(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new CustomException(LENGTH_EXCEEDED, "아이템 제목은 " + MAX_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    public boolean isMatch(String keyword) {
        return StringUtils.match(this.value, keyword);
    }
}
