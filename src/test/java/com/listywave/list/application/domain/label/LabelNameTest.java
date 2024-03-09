package com.listywave.list.application.domain.label;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LabelName은 ")
class LabelNameTest {

    @Test
    void 최대_제한_길이가_존재한다() {
        String value = IntStream.range(0, 10 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new LabelName(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }
}
