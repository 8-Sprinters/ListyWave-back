package com.listywave.user.application.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("프로필 설명은 ")
class DescriptionTest {

    @Test
    void 제한된_길이가_있다() {
        String value = IntStream.range(0, 160 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        assertThatThrownBy(() -> new Description(value));
    }

    @Test
    void 값이_같으면_동일한_객체이다() {
        Description description1 = new Description("설명입니다.");
        Description description2 = new Description("설명입니다.");

        assertThat(description1).isEqualTo(description2);
        assertThat(description1).hasSameHashCodeAs(description2);
    }
}
