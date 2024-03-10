package com.listywave.user.application.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("배경 이미지 URL은 ")
class BackgroundImageUrlTest {

    @Test
    void 최대_길이가_존재한다() {
        // given
        String value = IntStream.range(0, 2048 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        // then
        assertThatThrownBy(() -> new BackgroundImageUrl(value));
    }

    @Test
    void 값이_같으면_동일한_객체이다() {
        BackgroundImageUrl url1 = new BackgroundImageUrl("abcd");
        BackgroundImageUrl url2 = new BackgroundImageUrl("abcd");

        assertThat(url1).isEqualTo(url2);
        assertThat(url1).hasSameHashCodeAs(url2);
    }
}
