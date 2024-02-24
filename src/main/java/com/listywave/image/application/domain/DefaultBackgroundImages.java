package com.listywave.image.application.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultBackgroundImages {

    BACKGROUND_IMAGE_A("https://image.listywave.com/basic/listywave_background_a.png"),
    ;

    private final String value;

    public static String getRandomImageUrl() {
        ArrayList<DefaultBackgroundImages> images = new ArrayList<>(Arrays.stream(values()).toList());
        Collections.shuffle(images);
        return images.get(0).getValue();
    }
}
