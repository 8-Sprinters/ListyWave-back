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

@DisplayName("ItemImageUrl은 ")
class ItemImageUrlTest {

    @Test
    void 최대_제한_길이가_존재한다() {
        String value = IntStream.range(0, 2048 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new ItemImageUrl(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'', false",
            "' ', false",
            "https://~~, true"
    }, delimiterString = ", ")
    void 값이_존재하는지_여부를_반환한다(String value, boolean expect) {
        ItemImageUrl itemImageUrl = new ItemImageUrl(value);

        assertThat(itemImageUrl.hasValue()).isEqualTo(expect);
    }
}
