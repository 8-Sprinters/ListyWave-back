package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;

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
public class ItemImageUrl {

    private static final int LENGTH_LIMIT = 2048;

    @Column(name = "image_url", length = LENGTH_LIMIT, nullable = false)
    private final String value;

    public ItemImageUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value != null && value.length() > LENGTH_LIMIT) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, "아이템의 이미지 URL은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }

    public boolean hasValue() {
        return value != null && !value.isBlank();
    }
}
