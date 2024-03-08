package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.NICKNAME_LENGTH_EXCEEDED_EXCEPTION;
import static java.util.Objects.requireNonNull;

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
public class ItemComment {

    private static final int LENGTH_LIMIT = 100;

    @Column(name = "comment", length = LENGTH_LIMIT, nullable = false)
    private final String value;

    public ItemComment(String value) {
        requireNonNull(value, "Item Comment에 null 값이 포함되었습니다.");
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(NICKNAME_LENGTH_EXCEEDED_EXCEPTION, "아이템 comment는 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }

    public boolean isMatch(String keyword) {
        return StringUtils.match(this.value, keyword);
    }
}
