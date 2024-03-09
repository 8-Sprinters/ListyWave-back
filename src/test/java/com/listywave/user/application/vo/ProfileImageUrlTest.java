package com.listywave.user.application.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("프로필 이미지 URL은 ")
class ProfileImageUrlTest {

    @Test
    void 길이가_제한된다() {
        String value = IntStream.range(0, 2048 + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(""));

        assertThatThrownBy(() -> new ProfileImageUrl(value));
    }

    @Test
    void 값이_같으면_동일한_객체이다() {
        ProfileImageUrl profileImageUrl1 = new ProfileImageUrl("프로필 이미지 URL");
        ProfileImageUrl profileImageUrl2 = new ProfileImageUrl("프로필 이미지 URL");

        assertThat(profileImageUrl1).isEqualTo(profileImageUrl2);
        assertThat(profileImageUrl2).hasSameHashCodeAs(profileImageUrl2);
    }
}
