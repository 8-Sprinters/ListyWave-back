package com.listywave.user.application.vo;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;

import com.listywave.common.exception.CustomException;
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
public class ProfileImageUrl {

    private static final int LENGTH_LIMIT = 2048;

    @Column(name = "profile_image_url", nullable = false, length = LENGTH_LIMIT)
    private final String value;

    public ProfileImageUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, "프로필 이미지 URL은 " + LENGTH_LIMIT + "를 넘을 수 없습니다.");
        }
    }
}
