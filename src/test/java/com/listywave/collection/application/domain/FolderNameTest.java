package com.listywave.collection.application.domain;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.listywave.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DisplayName("FolderName은 ")
class FolderNameTest {

    @Test
    void 최대_제한_길이가_존재한다() {
        String value = IntStream.range(0, 30)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new FolderName(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }
}
