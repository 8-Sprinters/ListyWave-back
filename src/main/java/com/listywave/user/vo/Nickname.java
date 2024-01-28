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
public class Nickname {

    private static final int LENGTH_LIMIT = 17;

    @Column(name = "nickname", unique = true, length = 16)
    private final String value;

    public Nickname(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value.startsWith(" ") || value.endsWith(" ")) {
            throw new CustomException(ErrorCode.NICKNAME_CONTAINS_WHITESPACE, "닉네임의 처음과 마지막에 공백이 존재할 수 없습니다.");
        }
        if (value.length() >= LENGTH_LIMIT) {
            throw new CustomException(ErrorCode.LENGTH_EXCEEDED, "닉네임은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
        if (!value.matches("[가-힣a-zA-Z0-9]+")) {
            throw new CustomException(ErrorCode.NICKNAME_CONTAINS_SPECIAL_CHARACTERS, "닉네임에는 이모티콘 및 특수문자가 포함될 수 없습니다.");
        }
    }
}
