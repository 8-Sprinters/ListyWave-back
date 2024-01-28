package com.listywave.user.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("닉네임은 ")
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
class NicknameTest {

    @ParameterizedTest
    @ValueSource(strings = {" 닉네임", "닉네임 "})
    void 처음과_마지막에_공백이_들어갈_수_없다(String value) {
        // expect
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void 길이는_17자_이상일_수_없다() {
        // given
        String value = "12345678901234567";

        // expect
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"닉네임👍", "닉네임!", "*(&^*%&^$"})
    void 이모티콘_및_특수문자가_포함될_수_없다(String value) {
        // expect
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(RuntimeException.class);
    }
}
