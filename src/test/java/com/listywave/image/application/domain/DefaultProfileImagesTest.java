package com.listywave.image.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultProfileImagesTest {

    @Test
    void 무작위로_기본_프로필_이미지를_반환한다() {
        // when
        String result = DefaultProfileImages.getRandomImageUrl();

        // then
        List<String> allDefaultImageUrls = Arrays.stream(DefaultProfileImages.values())
                .map(DefaultProfileImages::getValue)
                .toList();
        assertThat(result).isIn(allDefaultImageUrls);
    }
}
