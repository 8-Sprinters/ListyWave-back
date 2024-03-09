package com.listywave.list.application.domain.comment;

import static com.listywave.common.exception.ErrorCode.LENGTH_EXCEEDED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CommentContent는 ")
class CommentContentTest {

    @Test
    void 제한된_길이가_존재한다() {
        String value = IntStream.range(0, 500 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        CustomException exception = assertThrows(CustomException.class, () -> new CommentContent(value));

        assertThat(exception.getErrorCode()).isEqualTo(LENGTH_EXCEEDED_EXCEPTION);
    }

    @Test
    void 값이_같으면_동일한_객체이다() {
        CommentContent content1 = new CommentContent("댓글");
        CommentContent content2 = new CommentContent("댓글");

        assertThat(content1).isEqualTo(content2);
        assertThat(content1).hasSameHashCodeAs(content2);
    }
}
