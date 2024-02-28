package com.listywave.image.application.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultBackgroundImages {

    BACKGROUND_IMAGE_A("https://image.listywave.com/basic/listywave_background_a.webp"),
    BACKGROUND_IMAGE_B("https://image.listywave.com/basic/listywave_background_b.webp"),
    BACKGROUND_IMAGE_C("https://image.listywave.com/basic/listywave_background_c.webp"),
    BACKGROUND_IMAGE_D("https://image.listywave.com/basic/listywave_background_d.webp"),
    BACKGROUND_IMAGE_E("https://image.listywave.com/basic/listywave_background_e.webp"),
    BACKGROUND_IMAGE_F("https://image.listywave.com/basic/listywave_background_f.webp"),
    BACKGROUND_IMAGE_G("https://image.listywave.com/basic/listywave_background_g.webp"),
    ;

    private final String value;

    public static String getRandomImageUrl() {
        ArrayList<DefaultBackgroundImages> images = new ArrayList<>(Arrays.stream(values()).toList());
        Collections.shuffle(images);
        return images.get(0).getValue();
    }
}
