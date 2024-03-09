package com.listywave.list.application.domain.list;

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

@DisplayName("ListDescription은 ")
class ListDescriptionTest {

    @Test
    void 최대_제한_길이가_존재한다() {
        String value = IntStream.range(0, 200 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new ListDescription(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "영국, true",
            "리스트, true",
            "' ', true",
            "중국, false",
            "이 리스트, true",
            "저 리스트, false"
    })
    void 주어진_문자열이_값에_포함되는지_여부를_반환한다(String keyword, boolean expect) {
        ListDescription listDescription = new ListDescription("이 리스트는 영국에서 온 리스트이며 ..");

        boolean result = listDescription.isMatch(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 값이_같으면_같은_객체이다() {
        ListDescription listDescription = new ListDescription("이 리스트는 영국에서 온 리스트이며 ..");
        ListDescription same = new ListDescription("이 리스트는 영국에서 온 리스트이며 ..");
        ListDescription different = new ListDescription("이 리스트는 중국에서 온 리스트이며 ..");

        assertThat(listDescription).isEqualTo(same);
        assertThat(listDescription).hasSameHashCodeAs(same);
        assertThat(listDescription).isNotEqualTo(different);
        assertThat(listDescription).doesNotHaveSameHashCodeAs(different);
    }
}
