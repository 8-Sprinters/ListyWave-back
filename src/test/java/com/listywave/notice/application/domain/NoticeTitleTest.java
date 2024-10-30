package com.listywave.notice.application.domain;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.NULL_OR_BLANK_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class NoticeTitleTest {

    @Test
    void 공지_제목의_최대_길이를_넘으면_예외를_발생한다() {
        // given
        String value = IntStream.range(0, 30)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        ErrorCode result = assertThrows(CustomException.class, () -> new NoticeTitle(value))
                .getErrorCode();

        // then
        assertThat(result).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 값이_null이거나_빈_값이면_예외를_발생한다(String value) {
        // when
        ErrorCode result = assertThrows(CustomException.class, () -> new NoticeTitle(value))
                .getErrorCode();

        // then
        assertThat(result).isEqualTo(NULL_OR_BLANK_EXCEPTION);
    }

    @Test
    void 공지_제목을_정상적으로_생성한다() {
        // given
        String value = "12345678911234567891123456789";

        // expect
        assertThatNoException().isThrownBy(() -> new NoticeTitle(value));
    }

    @Test
    void 값이_같으면_같은_객체다() {
        // given
        NoticeTitle title1 = new NoticeTitle("123456789");
        NoticeTitle title2 = new NoticeTitle("123456789");

        // expect
        assertThat(title1).isEqualTo(title2);
        assertThat(title1).hasSameHashCodeAs(title2);
    }
}
