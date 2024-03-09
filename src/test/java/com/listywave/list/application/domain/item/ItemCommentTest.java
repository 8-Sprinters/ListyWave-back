package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ItemComment는 ")
class ItemCommentTest {

    @Test
    void 최대_제한_길이가_존재한다() {
        String value = IntStream.range(0, 100 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new ItemComment(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "제일, true",
            "칼국수, true",
            "맛집, true",
            "싫어하는, false",
            "' ', true"
    })
    void 주어진_문자열이_값에_포함되는지_여부를_반환한다(String keyword, boolean expect) {
        ItemComment itemComment = new ItemComment("제일 좋아하는 칼국수 맛집 TOP5");

        boolean result = itemComment.isMatch(keyword);

        assertThat(result).isEqualTo(expect);
    }
}
