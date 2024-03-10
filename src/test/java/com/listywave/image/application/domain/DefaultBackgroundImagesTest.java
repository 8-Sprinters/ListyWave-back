package com.listywave.image.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultBackgroundImagesTest {

    @Test
    void 무작위로_배경_사진을_하나_반한한다() {
        // when
        String result = DefaultBackgroundImages.getRandomImageUrl();

        // then
        List<String> expects = Arrays.stream(DefaultBackgroundImages.values())
                .map(DefaultBackgroundImages::getValue)
                .toList();

        assertThat(result).isIn(expects);
    }
}
