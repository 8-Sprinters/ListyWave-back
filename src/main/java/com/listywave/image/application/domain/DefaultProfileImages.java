package com.listywave.image.application.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultProfileImages {

    PROFILE_IMAGE_A("https://image.listywave.com/basic/listywave_profile_a.webp"),
    PROFILE_IMAGE_B("https://image.listywave.com/basic/listywave_profile_b.webp"),
    PROFILE_IMAGE_C("https://image.listywave.com/basic/listywave_profile_c.webp"),
    PROFILE_IMAGE_D("https://image.listywave.com/basic/listywave_profile_d.webp"),
    PROFILE_IMAGE_E("https://image.listywave.com/basic/listywave_profile_e.webp"),
    ;

    private final String value;

    public static String getRandomImageUrl() {
        ArrayList<DefaultProfileImages> images = new ArrayList<>(Arrays.stream(values()).toList());
        Collections.shuffle(images);
        return images.get(0).getValue();
    }
}
