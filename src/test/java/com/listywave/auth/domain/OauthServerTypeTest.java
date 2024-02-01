package com.listywave.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OauthServerTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"kakao", "KAKAO"})
    void 대소문자_구분없이_enum의_이름으로_Enum_객체를_반환한다(String name) {
        // when
        OauthServerType result = OauthServerType.fromName(name);

        // then
        assertThat(result).isInstanceOf(OauthServerType.class);
    }

    @Test
    void 존재하지_않는_타입이라면_예외를_발생한다() {
        // given
        String name = "DAUM";

        // when
        assertThatIllegalArgumentException().isThrownBy(
                () -> OauthServerType.fromName(name)
        );
    }
}
