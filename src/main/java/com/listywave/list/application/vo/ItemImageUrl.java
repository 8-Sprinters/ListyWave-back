package com.listywave.list.application.vo;

import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ItemImageUrl {

    private static final int LENGTH_LIMIT = 2048;

    @Column(name = "image_url", nullable = false, length = LENGTH_LIMIT)
    private final String value;

    public ItemImageUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(ErrorCode.LENGTH_EXCEEDED, "아이템의 이미지 URL은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }
}
