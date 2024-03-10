package com.listywave.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("StringUtils는 ")
class StringUtilsTest {

    @ParameterizedTest
    @CsvSource(value = {
            "abcd, ab, true",
            "abcd, cd, true",
            "abcd, bc, true",
            "abcd, abcd, true",
            "여기 진짜 맛집임, 맛집, true",
            "으아아ㅏㅇ 배고프다, 맛집, false",
            "진짜 맛있는 거 먹고싶다, ' ', true",
            "가나다라마바사, ' ', false",
    }, delimiterString = ", ")
    void 문자열이_특정_문자열에_포함_여부를_반환한다(String source, String keyword, boolean expect) {
        boolean result = StringUtils.match(source, keyword);

        assertThat(result).isEqualTo(expect);
    }
}
