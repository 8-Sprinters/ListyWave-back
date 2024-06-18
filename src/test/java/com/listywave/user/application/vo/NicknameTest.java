package com.listywave.user.application.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("닉네임은 ")
class NicknameTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "aaaaabbbbb", "일", "일이삼사오육칠팔구십"})
    void 한영_포함_최소_1자에서_최대_10자이다(String value) {
        Nickname nickname = Nickname.of(value);

        assertThat(nickname.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "일이삼사오육칠팔구십일", "aaaaabbbbba"})
    void 길이가_유효하지_않으면_초기화할_수_없다(String value) {
        assertThatThrownBy(() -> Nickname.of(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", " 일이삼사오", "일이삼사오 ", " 일이삼사오 "})
    void 닉네임의_처음과_끝에_공백이_들어갈_수_없다(String value) {
        assertThatThrownBy(() -> Nickname.of(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"~!@#$%^&*()_+", "😃", "≒", ""})
    void 닉네임에는_이모티콘이나_특수문자가_포함될_수_없다(String value) {
        assertThatThrownBy(() -> Nickname.of(value));
    }

    @Test
    void 닉네임에_숫자가_포함될_수_없다() {
        assertThatThrownBy(() -> Nickname.of("z1존도적"));
    }

    @ParameterizedTest(name = "입력: {0}")
    @ValueSource(strings = {
            "listy", "ListyWave Official", "관리자", "운영자", "Admin", "시스템관리자", "Listywave관리자", "Listywave운영자",
            "ListyWaveOfficial", "리스티", "리스티웨이브관리자", "리스티웨이브운영자", "ADMIN", "admin", "팀 에잇", "EightOfficial",
            "eight", "EIGHT"
    })
    void 관리자와_헷갈릴만한_닉네임은_생성할_수_없다(String input) {
        assertThatThrownBy(() -> Nickname.of(input));
    }

    @Test
    void OauthId가_닉네임_최대_길이보다_긴_경우_최대_길이만큼_잘라_초기화한다() {
        Nickname nickname = Nickname.oauthIdOf("일이삼사오육칠팔구십일이삼사오");

        assertThat(nickname.getValue()).isEqualTo("일이삼사오육칠팔구십");
    }

    @Test
    void 값이_같으면_동일한_객체이다() {
        Nickname nickname1 = Nickname.of("nickname");
        Nickname nickname2 = Nickname.of("nickname");

        assertThat(nickname1).isEqualTo(nickname2);
        assertThat(nickname1).hasSameHashCodeAs(nickname2);
    }
}
