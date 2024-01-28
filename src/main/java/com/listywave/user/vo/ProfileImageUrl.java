package com.listywave.user.vo;

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
public class ProfileImageUrl {

    private static final int LENGTH_LIMIT = 2049;

    @Column(name = "profile_image_url", nullable = false, length = 2048)
    private final String value;

    public ProfileImageUrl(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.length() >= LENGTH_LIMIT) {
            throw new CustomException(ErrorCode.LENGTH_EXCEEDED, "프로필 이미지 URL은 " + LENGTH_LIMIT + "를 넘을 수 없습니다.");
        }
    }
}
