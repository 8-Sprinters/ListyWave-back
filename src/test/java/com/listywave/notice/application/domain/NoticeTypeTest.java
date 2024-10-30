package com.listywave.notice.application.domain;

import static com.listywave.common.exception.ErrorCode.NOT_EXIST_CODE;
import static com.listywave.notice.application.domain.NoticeType.NEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class NoticeTypeTest {

    @Test
    void 존재하지_않는_코드_번호로_생성하면_예외가_발생한다() {
        // given
        int code = 0;

        // when
        ErrorCode result = assertThrows(CustomException.class, () -> NoticeType.codeOf(code))
                .getErrorCode();

        // then
        assertThat(result).isEqualTo(NOT_EXIST_CODE);
    }

    @Test
    void 코드_번호로_생성한다() {
        // given
        int code = 1;

        // when
        NoticeType result = NoticeType.codeOf(code);

        // then
        assertThat(result).isEqualTo(NEWS);
    }
}
