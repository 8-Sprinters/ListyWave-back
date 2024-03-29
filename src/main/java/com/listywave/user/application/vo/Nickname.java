package com.listywave.user.application.vo;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.NICKNAME_CONTAINS_SPECIAL_CHARACTERS;
import static com.listywave.common.exception.ErrorCode.NICKNAME_CONTAINS_WHITESPACE_EXCEPTION;

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
public class Nickname {

    private static final int LENGTH_LIMIT = 10;

    @Column(name = "nickname", unique = true, length = LENGTH_LIMIT, nullable = false)
    private final String value;

    private Nickname(String value) {
        this.value = value;
    }

    public static Nickname of(String value) {
        validateBlank(value);
        validateLength(value);
        validateWord(value);
        return new Nickname(value);
    }

    public static Nickname oauthIdOf(String oauthId) {
        if (oauthId.length() > LENGTH_LIMIT) {
            return new Nickname(oauthId.substring(0, LENGTH_LIMIT));
        }
        return new Nickname(oauthId);
    }

    private static void validateBlank(String value) {
        if (value.startsWith(" ") || value.endsWith(" ")) {
            throw new CustomException(NICKNAME_CONTAINS_WHITESPACE_EXCEPTION, "닉네임의 처음과 마지막에 공백이 존재할 수 없습니다.");
        }
    }

    private static void validateLength(String value) {
        if (value.length() > LENGTH_LIMIT) {
            throw new CustomException(LENGTH_EXCEEDED_EXCEPTION, "닉네임은 " + LENGTH_LIMIT + "자를 넘을 수 없습니다.");
        }
    }

    private static void validateWord(String value) {
        if (!value.matches("[가-힣a-zA-Z]+")) {
            throw new CustomException(NICKNAME_CONTAINS_SPECIAL_CHARACTERS, "닉네임에는 숫자, 이모티콘, 특수문자가 포함될 수 없습니다.");
        }
    }
}
